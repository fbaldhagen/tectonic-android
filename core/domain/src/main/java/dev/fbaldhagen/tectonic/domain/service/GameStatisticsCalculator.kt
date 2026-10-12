package dev.fbaldhagen.tectonic.domain.service

import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GameStatistics
import dev.fbaldhagen.tectonic.domain.model.GameSummary
import java.time.ZoneId
import kotlin.time.Instant

interface GameStatisticsCalculator {

    /**
     * Computes statistics for [games], limited to [difficulty] when given.
     * [now] and [zoneId] decide which calendar day a game belongs to and whether a streak is alive.
     */
    fun calculate(
        games: List<GameSummary>,
        difficulty: Difficulty?,
        now: Instant,
        zoneId: ZoneId
    ): GameStatistics
}
