package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GameStatistics
import dev.fbaldhagen.tectonic.domain.repository.GameHistoryRepository
import dev.fbaldhagen.tectonic.domain.service.DomainClock
import dev.fbaldhagen.tectonic.domain.service.GameStatisticsCalculator
import java.time.ZoneId

class GetStatisticsUseCase(
    private val gameHistoryRepository: GameHistoryRepository,
    private val calculator: GameStatisticsCalculator,
    private val clock: DomainClock
) {
    /** Statistics for all difficulties, or only for [difficulty] when given. */
    suspend operator fun invoke(difficulty: Difficulty? = null): TecResult<GameStatistics> =
        when (val result = gameHistoryRepository.getFinishedGameSummaries()) {
            is TecResult.Success -> TecResult.Success(
                calculator.calculate(
                    games = result.data,
                    difficulty = difficulty,
                    now = clock.now(),
                    zoneId = ZoneId.systemDefault()
                )
            )

            is TecResult.Error -> result
        }
}
