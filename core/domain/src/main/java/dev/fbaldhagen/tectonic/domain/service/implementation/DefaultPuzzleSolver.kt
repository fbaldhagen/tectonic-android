package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.service.PuzzleSolver

class DefaultPuzzleSolver : PuzzleSolver {

    override fun solve(puzzle: Puzzle): TecResult<List<Map<CellId, Int>>> = try {
        val constraints = BoardConstraints.from(puzzle)
        val solutions = solverFor(puzzle, constraints).findSolutions(Int.MAX_VALUE)
        TecResult.Success(solutions.map { it.toSolutionMap(constraints) })
    } catch (e: Exception) {
        TecResult.Error(AppError.Unknown("Solver failed: ${e.message}", e))
    }

    override fun countSolutions(puzzle: Puzzle, limit: Int): Int =
        solverFor(puzzle).findSolutions(limit).size

    override fun findOneSolution(puzzle: Puzzle): Map<CellId, Int>? {
        val constraints = BoardConstraints.from(puzzle)
        return solverFor(puzzle, constraints)
            .findSolutions(limit = 1)
            .firstOrNull()
            ?.toSolutionMap(constraints)
    }

    override fun isSolvableByLogic(puzzle: Puzzle): Boolean =
        solverFor(puzzle).solvesByLogicAlone()

    private fun solverFor(
        puzzle: Puzzle,
        constraints: BoardConstraints = BoardConstraints.from(puzzle)
    ) = LogicalBoardSolver(constraints, constraints.givensOf(puzzle))

    private fun IntArray.toSolutionMap(constraints: BoardConstraints): Map<CellId, Int> =
        indices.associate { index -> constraints.cellIdOf(index) to this[index] }
}
