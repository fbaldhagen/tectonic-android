package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository

class GetGameUseCase(
    private val gameProgressRepository: GameProgressRepository
) {
    suspend operator fun invoke(gameId: GameId): TecResult<Game?> =
        gameProgressRepository.getGame(gameId)
}