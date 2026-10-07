package dev.fbaldhagen.tectonic.domain.service

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Puzzle

interface PuzzleSolver {
    /** Finds all solutions of [puzzle]. */
    fun solve(puzzle: Puzzle): TecResult<List<Map<CellId, Int>>>

    /**
     * Counts solutions up to [limit]. Stops searching once the limit is reached,
     * so it is cheap for uniqueness checks (limit = 2).
     */
    fun countSolutions(puzzle: Puzzle, limit: Int = 2): Int

    /** Finds a single solution, or null if the puzzle has none. Deterministic. */
    fun findOneSolution(puzzle: Puzzle): Map<CellId, Int>?

    /**
     * True when the puzzle can be completed by deduction alone (naked and hidden
     * singles), without guessing. Implies a unique solution.
     */
    fun isSolvableByLogic(puzzle: Puzzle): Boolean
}