package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.DifficultyTimes
import dev.fbaldhagen.tectonic.domain.model.GameStatistics
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.GameSummary
import dev.fbaldhagen.tectonic.domain.service.GameStatisticsCalculator
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.Instant

class DefaultGameStatisticsCalculator : GameStatisticsCalculator {

    override fun calculate(
        games: List<GameSummary>,
        difficulty: Difficulty?,
        now: Instant,
        zoneId: ZoneId
    ): GameStatistics {
        val inScope = games.filter { difficulty == null || it.difficulty == difficulty }
        val completedGames = inScope.filter { it.status == GameStatus.COMPLETED }
        val completed = completedGames.size
        val abandoned = inScope.count { it.status == GameStatus.ABANDONED }
        val finished = completed + abandoned

        val timedGames = completedGames.filter { it.duration != null }
        val playDays = completedGames.map { it.finishedAt.toLocalDate(zoneId) }.toSortedSet()

        return GameStatistics(
            played = finished,
            completed = completed,
            abandoned = abandoned,
            completionRate = if (finished == 0) null else completed.toDouble() / finished,
            totalTime = timedGames.fold(Duration.ZERO) { total, game -> total + game.duration!! },
            times = (difficulty?.let { listOf(it) } ?: Difficulty.entries)
                .map { timesFor(it, timedGames) },
            currentStreakDays = currentStreak(playDays, now.toLocalDate(zoneId)),
            longestStreakDays = longestStreak(playDays),
            recentSolveTimes = timedGames
                .sortedBy { it.finishedAt }
                .takeLast(RECENT_SOLVE_LIMIT)
                .map { it.duration!! }
        )
    }

    private fun timesFor(difficulty: Difficulty, timedGames: List<GameSummary>): DifficultyTimes {
        val durations = timedGames.filter { it.difficulty == difficulty }.map { it.duration!! }
        return DifficultyTimes(
            difficulty = difficulty,
            completed = durations.size,
            best = durations.minOrNull(),
            average = if (durations.isEmpty()) null else durations.fold(Duration.ZERO, Duration::plus) / durations.size
        )
    }

    /** A streak is still alive today if the last completed game was today or yesterday. */
    private fun currentStreak(playDays: Set<LocalDate>, today: LocalDate): Int {
        var day = when {
            today in playDays -> today
            today.minusDays(1) in playDays -> today.minusDays(1)
            else -> return 0
        }
        var length = 0
        while (day in playDays) {
            length++
            day = day.minusDays(1)
        }
        return length
    }

    private fun longestStreak(playDays: Set<LocalDate>): Int {
        var longest = 0
        var run = 0
        var previous: LocalDate? = null
        for (day in playDays) {
            run = if (previous != null && previous.plusDays(1) == day) run + 1 else 1
            longest = maxOf(longest, run)
            previous = day
        }
        return longest
    }

    private fun Instant.toLocalDate(zoneId: ZoneId): LocalDate =
        java.time.Instant.ofEpochMilli(toEpochMilliseconds()).atZone(zoneId).toLocalDate()

    companion object {
        const val RECENT_SOLVE_LIMIT = 20
    }
}
