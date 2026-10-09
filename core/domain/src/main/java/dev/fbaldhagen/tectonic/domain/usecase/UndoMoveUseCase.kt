package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository

class UndoMoveUseCase(
    private val gameProgressRepository: GameProgressRepository
) {
    suspend operator fun invoke(
        gameId: GameId
    ): TecResult<Game> {
        return when (val result = gameProgressRepository.getGame(gameId)) {
            is TecResult.Success -> {
                val game = result.data
                if (game.status != GameStatus.IN_PROGRESS) {
                    return TecResult.Error(AppError.GameNotInProgress)
                }

                val lastMove = game.state.moves.lastOrNull()
                    ?: return TecResult.Error(AppError.NothingToUndo)

                val updatedState = game.state.copy(
                    values = game.state.values + (
                            lastMove.cellId to lastMove.previousValue
                            ),
                    moves = game.state.moves.dropLast(1)
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