package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.service.PuzzleSolver
import kotlin.random.Random

/** Removes givens from a fully solved puzzle while the puzzle stays acceptable. */
internal class ClueReducer(
    private val solver: PuzzleSolver,
    private val random: Random
) {
    /**
     * Greedily removes givens in random order until [targetClues] remain or no further
     * removal is possible. With [logicOnly] a removal is kept only if the puzzle can still
     * be solved without guessing, otherwise it only has to stay uniquely solvable.
     */
    fun reduce(solved: Puzzle, targetClues: Int, logicOnly: Boolean): Puzzle {
        val cells = solved.cells.map { it.copy(givenValue = solution(solved, it.id)) }.toMutableList()
        var clueCount = cells.size

        for (index in cells.indices.shuffled(random)) {
            if (clueCount <= targetClues) break

            val original = cells[index]
            cells[index] = original.copy(givenValue = null)

            val candidate = solved.copy(cells = cells.toList())
            val acceptable = if (logicOnly) {
                solver.isSolvableByLogic(candidate)
            } else {
                solver.countSolutions(candidate, limit = 2) == 1
            }

            if (acceptable) clueCount-- else cells[index] = original
        }
        return solved.copy(cells = cells.toList())
    }

    private fun solution(puzzle: Puzzle, id: CellId): Int =
        puzzle.solution.getValue(id)
}
