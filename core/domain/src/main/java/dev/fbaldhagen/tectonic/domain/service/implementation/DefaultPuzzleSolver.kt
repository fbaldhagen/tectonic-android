package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.service.PuzzleSolver
import kotlin.random.Random

class DefaultPuzzleSolver(
    private val random: Random = Random.Default
) : PuzzleSolver {

    /**
     * Finds all solutions. For uniqueness testing the generator will usually
     * stop after finding the second solution.
     */
    override fun solve(puzzle: Puzzle): TecResult<List<Map<CellId, Int>>> {
        return try {
            val solutions = findAllSolutions(puzzle, limit = Int.MAX_VALUE)
            TecResult.Success(solutions)
        } catch (e: Exception) {
            TecResult.Error(AppError.Unknown("Solver failed: ${e.message}", e))
        }
    }

    /**
     * Convenience for the generator: count solutions up to [limit] (normally 2).
     */
    override fun countSolutions(puzzle: Puzzle, limit: Int): Int {
        return findAllSolutions(puzzle, limit).size
    }

    /**
     * Find a single solution (randomized candidate order).
     * Useful when the generator needs any valid filling of a region layout.
     */
    override fun findOneSolution(puzzle: Puzzle): Map<CellId, Int>? {
        return findAllSolutions(puzzle, limit = 1).firstOrNull()
    }

    // -------------------------------------------------------------------------
    // Core search
    // -------------------------------------------------------------------------

    private fun findAllSolutions(puzzle: Puzzle, limit: Int): List<Map<CellId, Int>> {
        val ctx = SolverContext.from(puzzle)
        val solutions = mutableListOf<Map<CellId, Int>>()

        fun search() {
            if (solutions.size >= limit) return

            when (val next = ctx.selectNext()) {
                is NextCell.Complete -> solutions += ctx.snapshot()
                is NextCell.DeadEnd -> return
                is NextCell.Found -> {
                    val candidates = ctx.candidates(next.cell)
                    candidates.shuffle(random)
                    for (value in candidates) {
                        ctx.place(next.cell, value)
                        search()
                        ctx.remove(next.cell, value)
                        if (solutions.size >= limit) return
                    }
                }
            }
        }

        search()
        return solutions
    }

    private sealed interface NextCell {
        data class Found(val cell: CellId) : NextCell
        data object Complete : NextCell
        data object DeadEnd : NextCell
    }

    // -------------------------------------------------------------------------
    // Internal context
    // -------------------------------------------------------------------------

    private class SolverContext private constructor(
        private val cells: List<CellId>,
        private val regionOf: Map<CellId, Int>,          // cell -> region index
        private val regionSizes: IntArray,
        private val adjacency: Map<CellId, List<CellId>>,
        private val board: MutableMap<CellId, Int?>,
        private val usedInRegion: Array<MutableSet<Int>>
    ) {
        fun selectNext(): NextCell {
            var best: CellId? = null
            var bestCount = Int.MAX_VALUE

            for (cell in cells) {
                if (board[cell] != null) continue
                val count = candidates(cell).size
                if (count == 0) return NextCell.DeadEnd
                if (count < bestCount) {
                    bestCount = count
                    best = cell
                    if (bestCount == 1) break
                }
            }
            return if (best == null) NextCell.Complete else NextCell.Found(best)
        }

        fun candidates(cell: CellId): MutableList<Int> {
            val ri = regionOf.getValue(cell)
            val size = regionSizes[ri]
            val used = usedInRegion[ri]
            val result = mutableListOf<Int>()

            for (v in 1..size) {
                if (v in used) continue
                if (adjacency.getValue(cell).any { board[it] == v }) continue
                result += v
            }
            return result
        }

        fun place(cell: CellId, value: Int) {
            board[cell] = value
            usedInRegion[regionOf.getValue(cell)].add(value)
        }

        fun remove(cell: CellId, value: Int) {
            board[cell] = null
            usedInRegion[regionOf.getValue(cell)].remove(value)
        }

        fun snapshot(): Map<CellId, Int> =
            board.mapValues { (_, v) -> v!! }

        companion object {
            fun from(puzzle: Puzzle): SolverContext {
                val cells = puzzle.cells.map { it.id }
                val regionIndexById = puzzle.regions.mapIndexed { idx, r -> r.id to idx }.toMap()

                val regionOf = mutableMapOf<CellId, Int>()
                puzzle.cells.forEach { cell ->
                    regionOf[cell.id] = regionIndexById.getValue(cell.regionId)
                }

                val regionSizes = IntArray(puzzle.regions.size) { puzzle.regions[it].cells.size }

                val adjacency = buildAdjacency(puzzle.gridSize)

                val board = mutableMapOf<CellId, Int?>()
                val usedInRegion = Array(puzzle.regions.size) { mutableSetOf<Int>() }

                // Seed with givens
                for (cell in puzzle.cells) {
                    val given = cell.givenValue
                    board[cell.id] = given
                    if (given != null) {
                        val ri = regionOf.getValue(cell.id)
                        usedInRegion[ri].add(given)
                    }
                }

                return SolverContext(
                    cells = cells,
                    regionOf = regionOf,
                    regionSizes = regionSizes,
                    adjacency = adjacency,
                    board = board,
                    usedInRegion = usedInRegion
                )
            }

            private fun buildAdjacency(gridSize: GridSize): Map<CellId, List<CellId>> {
                val result = mutableMapOf<CellId, List<CellId>>()
                for (r in 0 until gridSize.rows) {
                    for (c in 0 until gridSize.columns) {
                        val id = CellId(r, c)
                        val neighbors = mutableListOf<CellId>()
                        for (dr in -1..1) {
                            for (dc in -1..1) {
                                if (dr == 0 && dc == 0) continue
                                val nr = r + dr
                                val nc = c + dc
                                if (nr in 0 until gridSize.rows && nc in 0 until gridSize.columns) {
                                    neighbors += CellId(nr, nc)
                                }
                            }
                        }
                        result[id] = neighbors
                    }
                }
                return result
            }
        }
    }
}