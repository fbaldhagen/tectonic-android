package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Cell
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleId
import dev.fbaldhagen.tectonic.domain.model.Region
import dev.fbaldhagen.tectonic.domain.model.RegionId
import dev.fbaldhagen.tectonic.domain.service.PuzzleGenerator
import dev.fbaldhagen.tectonic.domain.service.PuzzleSolver
import dev.fbaldhagen.tectonic.domain.service.PuzzleValidator
import kotlin.random.Random

/**
 * Deterministic for a given (difficulty, seed): all randomness comes from one seeded
 * [Random] and all limits are node budgets, never wall-clock time.
 */
class DefaultPuzzleGenerator(
    private val solver: PuzzleSolver,
    private val validator: PuzzleValidator
) : PuzzleGenerator {

    override fun generate(difficulty: Difficulty, seed: Long): TecResult<Puzzle> {
        val random = Random(seed)
        val profile = DifficultyProfile.of(difficulty)
        val topology = BoardTopology.of(profile.gridSize)
        val builder = ConstructivePuzzleBuilder(topology, random, BUILDER_NODE_BUDGET)
        val reducer = ClueReducer(solver, random)

        var lastFailure = "no attempt was made"
        for (attempt in 1..MAX_ATTEMPTS) {
            val board = builder.build()
            if (board == null) {
                lastFailure = "board construction exceeded its search budget"
                continue
            }

            val solved = assemble(board, profile, difficulty, seed)
            val reduced = reducer.reduce(solved, targetClues(profile, random), profile.requiresLogicalSolution)

            val clueCount = reduced.cells.count { it.givenValue != null }
            if (clueCount > maxClues(profile)) {
                lastFailure = "could not remove enough givens for $difficulty"
                continue
            }

            return when (val validation = validator.validate(reduced)) {
                is TecResult.Success -> TecResult.Success(reduced)
                is TecResult.Error -> validation
            }
        }

        return TecResult.Error(
            AppError.Unknown("Puzzle generation failed after $MAX_ATTEMPTS attempts: $lastFailure")
        )
    }

    private fun targetClues(profile: DifficultyProfile, random: Random): Int {
        val range = profile.clueRatio
        val ratio = range.start + random.nextDouble() * (range.endInclusive - range.start)
        return (profile.cellCount * ratio).toInt().coerceAtLeast(1)
    }

    private fun maxClues(profile: DifficultyProfile): Int =
        (profile.cellCount * profile.clueRatio.endInclusive).toInt()

    private fun assemble(
        board: ConstructedBoard,
        profile: DifficultyProfile,
        difficulty: Difficulty,
        seed: Long
    ): Puzzle {
        val columns = profile.gridSize.columns
        fun cellId(index: Int) = CellId(index / columns, index % columns)

        val regionOfCell = IntArray(profile.cellCount)
        board.regions.forEachIndexed { regionIndex, cells ->
            cells.forEach { regionOfCell[it] = regionIndex }
        }

        return Puzzle(
            id = PuzzleId("gen-${difficulty.name.lowercase()}-${seed.toString(16)}"),
            gridSize = profile.gridSize,
            cells = List(profile.cellCount) { index ->
                Cell(
                    id = cellId(index),
                    regionId = RegionId(regionOfCell[index]),
                    givenValue = null
                )
            },
            regions = board.regions.mapIndexed { regionIndex, cells ->
                Region(id = RegionId(regionIndex), cells = cells.map(::cellId).toSet())
            },
            solution = List(profile.cellCount) { index -> cellId(index) to board.solution[index] }.toMap(),
            difficulty = difficulty
        )
    }

    private companion object {
        const val MAX_ATTEMPTS = 30

        /**
         * Backtracking has a heavy tail, so many cheap attempts beat one long one:
         * about 20 ms and a two-in-three success rate per attempt on 8x8.
         */
        const val BUILDER_NODE_BUDGET = 5_000
    }
}
