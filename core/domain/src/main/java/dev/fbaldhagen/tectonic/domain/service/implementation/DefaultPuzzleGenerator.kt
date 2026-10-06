package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Cell
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleId
import dev.fbaldhagen.tectonic.domain.model.Region
import dev.fbaldhagen.tectonic.domain.model.RegionId
import dev.fbaldhagen.tectonic.domain.service.PuzzleGenerator
import dev.fbaldhagen.tectonic.domain.service.PuzzleValidator
import kotlin.random.Random

class DefaultPuzzleGenerator(
    private val solver: DefaultPuzzleSolver,
    private val validator: PuzzleValidator = DefaultPuzzleValidator()
) : PuzzleGenerator {

    override fun generate(difficulty: Difficulty, seed: Long): TecResult<Puzzle> {
        val random = Random(seed)

        return try {
            val gridSize = gridSizeFor(difficulty)
            val regions = generateRegions(gridSize, random)
            val emptyPuzzle = buildEmptyPuzzle(gridSize, regions, difficulty, seed)

            // 1. Find any complete valid filling
            val solution = solver.findOneSolution(emptyPuzzle)
                ?: return TecResult.Error(AppError.Unknown("Could not find a solution for the generated layout"))

            // 2. Create a fully filled puzzle
            val fullPuzzle = emptyPuzzle.copy(
                solution = solution,
                cells = emptyPuzzle.cells.map { cell ->
                    cell.copy(givenValue = solution[cell.id])
                }
            )

            // 3. Remove clues according to difficulty while keeping uniqueness
            val finalPuzzle = removeClues(fullPuzzle, difficulty, random)

            // 4. Final validation
            when (val validation = validator.validate(finalPuzzle)) {
                is TecResult.Success -> TecResult.Success(finalPuzzle)
                is TecResult.Error -> validation
            }
        } catch (e: Exception) {
            TecResult.Error(AppError.Unknown("Puzzle generation failed: ${e.message}", e))
        }
    }

    // -------------------------------------------------------------------------
    // Grid size & clue targets
    // -------------------------------------------------------------------------

    private fun gridSizeFor(difficulty: Difficulty): GridSize = when (difficulty) {
        Difficulty.EASY -> GridSize(5, 5)
        Difficulty.MEDIUM -> GridSize(6, 6)
        Difficulty.HARD -> GridSize(8, 8)
    }

    /** Approximate fraction of cells that should remain as givens */
    private fun targetClueRatio(difficulty: Difficulty): Double = when (difficulty) {
        Difficulty.EASY -> 0.50
        Difficulty.MEDIUM -> 0.35
        Difficulty.HARD -> 0.22
    }

    // -------------------------------------------------------------------------
    // Region generation (seeded polyomino partition)
    // -------------------------------------------------------------------------

    private fun generateRegions(gridSize: GridSize, random: Random): List<Region> {
        val remaining = mutableSetOf<CellId>()
        for (r in 0 until gridSize.rows) {
            for (c in 0 until gridSize.columns) {
                remaining += CellId(r, c)
            }
        }

        val regions = mutableListOf<Region>()
        var nextRegionId = 0

        while (remaining.isNotEmpty()) {
            val start = remaining.random(random)
            val maxSize = minOf(5, remaining.size)
            // Prefer sizes 2–4 for nicer looking puzzles
            val desiredSize = when {
                maxSize == 1 -> 1
                else -> (2..maxSize).random(random)
            }

            val regionCells = growRegion(start, desiredSize, remaining, gridSize, random)
            remaining.removeAll(regionCells)

            regions += Region(
                id = RegionId(nextRegionId++),
                cells = regionCells
            )
        }

        return regions
    }

    private fun growRegion(
        start: CellId,
        desiredSize: Int,
        available: Set<CellId>,
        gridSize: GridSize,
        random: Random
    ): Set<CellId> {
        val region = mutableSetOf(start)
        val frontier = ArrayDeque<CellId>().apply { add(start) }

        while (region.size < desiredSize && frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            val candidates = getOrthogonalNeighbors(current, gridSize)
                .filter { it in available && it !in region }
                .shuffled(random)

            for (candidate in candidates) {
                if (region.size >= desiredSize) break
                region += candidate
                frontier.addLast(candidate)
            }
        }

        return region
    }

    private fun getOrthogonalNeighbors(cell: CellId, gridSize: GridSize): List<CellId> {
        val result = mutableListOf<CellId>()
        val deltas = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        for ((dr, dc) in deltas) {
            val r = cell.row + dr
            val c = cell.column + dc
            if (r in 0 until gridSize.rows && c in 0 until gridSize.columns) {
                result += CellId(r, c)
            }
        }
        return result
    }

    // -------------------------------------------------------------------------
    // Build helpers
    // -------------------------------------------------------------------------

    private fun buildEmptyPuzzle(
        gridSize: GridSize,
        regions: List<Region>,
        difficulty: Difficulty,
        seed: Long
    ): Puzzle {
        val cellToRegion = mutableMapOf<CellId, RegionId>()
        regions.forEach { region ->
            region.cells.forEach { cellId -> cellToRegion[cellId] = region.id }
        }

        val cells = mutableListOf<Cell>()
        for (r in 0 until gridSize.rows) {
            for (c in 0 until gridSize.columns) {
                val id = CellId(r, c)
                cells += Cell(
                    id = id,
                    regionId = cellToRegion.getValue(id),
                    givenValue = null
                )
            }
        }

        return Puzzle(
            id = PuzzleId("gen-${seed.toString(16)}"),
            gridSize = gridSize,
            cells = cells,
            regions = regions,
            solution = emptyMap(),
            difficulty = difficulty
        )
    }

    // -------------------------------------------------------------------------
    // Clue removal with uniqueness guarantee
    // -------------------------------------------------------------------------

    private fun removeClues(
        fullPuzzle: Puzzle,
        difficulty: Difficulty,
        random: Random
    ): Puzzle {
        val targetRatio = targetClueRatio(difficulty)
        val totalCells = fullPuzzle.cells.size
        val targetClues = (totalCells * targetRatio).toInt().coerceAtLeast(1)

        // Start with every cell given, then try to remove
        val currentGivens = fullPuzzle.cells.associate { it.id to it.givenValue!! }.toMutableMap()
        val candidates = fullPuzzle.cells.map { it.id }.shuffled(random)

        for (cellId in candidates) {
            if (currentGivens.size <= targetClues) break

            // Temporarily remove
            val saved = currentGivens.remove(cellId)!!

            val candidatePuzzle = fullPuzzle.copy(
                cells = fullPuzzle.cells.map { cell ->
                    cell.copy(givenValue = currentGivens[cell.id])
                },
                solution = fullPuzzle.solution
            )

            // Keep the removal only if the puzzle stays uniquely solvable
            val solutionCount = solver.countSolutions(candidatePuzzle, limit = 2)
            if (solutionCount != 1) {
                // Restore
                currentGivens[cellId] = saved
            }
        }

        return fullPuzzle.copy(
            cells = fullPuzzle.cells.map { cell ->
                cell.copy(givenValue = currentGivens[cell.id])
            }
        )
    }
}