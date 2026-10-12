package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.repository.GameTimeRepository
import kotlin.time.Duration

class SaveElapsedTimeUseCase(
    private val gameTimeRepository: GameTimeRepository
) {
    suspend operator fun invoke(gameId: GameId, elapsed: Duration): TecResult<Unit> =
        gameTimeRepository.saveElapsed(gameId, elapsed)
}
