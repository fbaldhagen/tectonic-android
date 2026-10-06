package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleState
import dev.fbaldhagen.tectonic.domain.model.SolutionCheckResult
import dev.fbaldhagen.tectonic.domain.model.SolutionStatus
import dev.fbaldhagen.tectonic.domain.service.SolutionChecker

class DefaultSolutionChecker : SolutionChecker {

    override fun check(puzzle: Puzzle, state: PuzzleState): SolutionCheckResult {
        val incorrect = mutableSetOf<CellId>()
        var hasEmpty = false

        for (cell in puzzle.cells) {
            val current = state.values[cell.id]
            val expected = puzzle.solution[cell.id]

            when {
                current == null -> hasEmpty = true
                current != expected -> incorrect += cell.id
            }
        }

        val status = when {
            incorrect.isNotEmpty() -> SolutionStatus.INCORRECT
            hasEmpty -> SolutionStatus.INCOMPLETE
            else -> SolutionStatus.SOLVED
        }

        return SolutionCheckResult(
            status = status,
            incorrectCells = incorrect
        )
    }
}