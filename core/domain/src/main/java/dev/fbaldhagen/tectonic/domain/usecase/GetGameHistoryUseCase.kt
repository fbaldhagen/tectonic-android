package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.GameSummary
import dev.fbaldhagen.tectonic.domain.repository.GameHistoryRepository

class GetGameHistoryUseCase(
    private val gameHistoryRepository: GameHistoryRepository
) {
    suspend operator fun invoke(): TecResult<List<GameSummary>> =
        gameHistoryRepository.getFinishedGameSummaries()
}