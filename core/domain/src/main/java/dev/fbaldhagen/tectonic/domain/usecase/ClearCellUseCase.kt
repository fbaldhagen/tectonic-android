package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository

class ClearCellUseCase(
    private val gameProgressRepository: GameProgressRepository
) {
    suspend operator fun invoke(
        gameId: GameId,
        cellId: CellId
    ): TecResult<Game> {
        return when (val result = gameProgressRepository.getGame(gameId)) {
            is TecResult.Success -> {
                val game = result.data
                if (game.status != GameStatus.IN_PROGRESS) {
                    return TecResult.Error(AppError.GameNotInProgress)
                }

                val cell = game.puzzle.cells.firstOrNull { it.id == cellId }

                if (cell == null) {
                    return TecResult.Error(AppError.CellNotFound(cellId.row, cellId.column))
                }

                if (cell.givenValue != null) {
                    return TecResult.Error(AppError.CellNotEditable(cellId.row, cellId.column))
                }

                val updatedState = game.state.copy(
                    values = game.state.values + (cellId to null)
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