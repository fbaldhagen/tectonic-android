package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.Move
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository
import dev.fbaldhagen.tectonic.domain.service.MoveValidator

class MakeMoveUseCase(
    private val gameProgressRepository: GameProgressRepository,
    private val moveValidator: MoveValidator
) {
    suspend operator fun invoke(
        gameId: GameId,
        cellId: CellId,
        value: Int
    ): TecResult<Game> {
        return when (val result = gameProgressRepository.getGame(gameId)) {
            is TecResult.Success -> {
                val game = result.data
                val validation = moveValidator.validate(
                    puzzle = game.puzzle,
                    state = game.state,
                    cellId = cellId,
                    value = value
                )

                if (!validation.isValid) {
                    return TecResult.Error(
                        AppError.InvalidMove(validation.violations.map { it.name}.toList())
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

                val updatedGame = game.copy(state = updatedState)

                when (val saveResult = gameProgressRepository.saveGame(updatedGame)) {
                    is TecResult.Success -> TecResult.Success(updatedGame)
                    is TecResult.Error -> saveResult
                }
            }

            is TecResult.Error -> result
        }
    }
}