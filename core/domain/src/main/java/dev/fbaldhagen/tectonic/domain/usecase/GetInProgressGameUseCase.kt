package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.repository.GameHistoryRepository

class GetInProgressGameUseCase(
    private val gameHistoryRepository: GameHistoryRepository
) {
    suspend operator fun invoke(): TecResult<Game?> =
        gameHistoryRepository.getInProgressGame()
}