package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository

class GetCurrentGameUseCase(
    private val gameProgressRepository: GameProgressRepository
) {
    suspend operator fun invoke(): TecResult<Game?> =
        gameProgressRepository.getMostRecentGame()
}