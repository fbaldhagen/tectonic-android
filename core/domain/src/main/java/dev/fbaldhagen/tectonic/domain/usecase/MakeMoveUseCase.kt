package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameResult
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.Move
import dev.fbaldhagen.tectonic.domain.model.SolutionStatus
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository
import dev.fbaldhagen.tectonic.domain.service.DomainClock
import dev.fbaldhagen.tectonic.domain.service.MoveValidator
import dev.fbaldhagen.tectonic.domain.service.SolutionChecker

class MakeMoveUseCase(
    private val gameProgressRepository: GameProgressRepository,
    private val moveValidator: MoveValidator,
    private val solutionChecker: SolutionChecker,
    private val clock: DomainClock
) {
    suspend operator fun invoke(
        gameId: GameId,
        cellId: CellId,
        value: Int
    ): TecResult<Game> {
        return when (val result = gameProgressRepository.getGame(gameId)) {
            is TecResult.Success -> {
                val game = result.data
                if (game.status != GameStatus.IN_PROGRESS) {
                    return TecResult.Error(AppError.GameNotInProgress)
                }

                val validation = moveValidator.validate(
                    puzzle = game.puzzle,
                    state = game.state,
                    cellId = cellId,
                    value = value
                )

                if (!validation.isValid) {
                    return TecResult.Error(
                        AppError.InvalidMove(validation.violations.map { it.name }.toList())
                    )
                }

                val move = Move(
                    cellId = cellId,
                    previousValue = game.state.values[cellId],
                    newValue = value
                )

                val updatedState = game.state.copy(
                    values = game.state.values + (cellId to value),
                    moves = game.state.moves + move
                )

                val updatedGame = game.copy(state = updatedState).completedIfSolved()

                when (val saveResult = gameProgressRepository.saveGame(updatedGame)) {
                    is TecResult.Success -> TecResult.Success(updatedGame)
                    is TecResult.Error -> saveResult
                }
            }

            is TecResult.Error -> result
        }
    }

    private fun Game.completedIfSolved(): Game {
        val check = solutionChecker.check(puzzle = puzzle, state = state)
        if (check.status != SolutionStatus.SOLVED) return this

        val completedAt = clock.now()
        return copy(
            status = GameStatus.COMPLETED,
            // Mistakes and hints are not tracked yet, so they are always zero.
            result = GameResult(
                completedAt = completedAt,
                duration = completedAt - startedAt,
                mistakes = 0,
                hintsUsed = 0
            )
        )
    }
}