package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository

class AbandonGameUseCase(
    private val gameProgressRepository: GameProgressRepository
) {
    suspend operator fun invoke(gameId: GameId): TecResult<Game> {
        return when (val result = gameProgressRepository.getGame(gameId)) {
            is TecResult.Success -> {
                val game = result.data
                if (game.status != GameStatus.IN_PROGRESS) {
                    return TecResult.Error(AppError.GameNotInProgress)
                }

                val abandoned = game.copy(status = GameStatus.ABANDONED)

                when (val saveResult = gameProgressRepository.saveGame(abandoned)) {
                    is TecResult.Success -> TecResult.Success(abandoned)
                    is TecResult.Error -> saveResult
                }
            }

            is TecResult.Error -> result
        }
    }
}
