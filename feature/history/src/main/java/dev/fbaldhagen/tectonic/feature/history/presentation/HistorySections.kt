package dev.fbaldhagen.tectonic.feature.history.presentation

import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.GameSummary
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.WeekFields
import kotlin.time.Instant

sealed interface HistorySectionKey {
    data object Today : HistorySectionKey
    data object Yesterday : HistorySectionKey
    data object ThisWeek : HistorySectionKey
    data class Month(val month: YearMonth) : HistorySectionKey
}

data class HistoryItem(
    val game: GameSummary,
    /** True for the single fastest completed game of its difficulty. */
    val isPersonalBest: Boolean,
)

data class HistorySection(
    val key: HistorySectionKey,
    val items: List<HistoryItem>,
) {
    /** A string key that survives saved-state restoration, which lazy list keys require. */
    val id: String
        get() = when (key) {
            HistorySectionKey.Today -> "today"
            HistorySectionKey.Yesterday -> "yesterday"
            HistorySectionKey.ThisWeek -> "this-week"
            is HistorySectionKey.Month -> "month-${key.month}"
        }
}

/**
 * Groups finished games into Today, Yesterday, This week and then one section per month,
 * newest first. "This week" follows [weekFields], so it respects the locale's first weekday.
 */
internal fun groupHistory(
    games: List<GameSummary>,
    now: Instant,
    zone: ZoneId,
    weekFields: WeekFields,
): List<HistorySection> {
    val today = now.toLocalDate(zone)
    val bestByDifficulty = personalBests(games)

    return games
        .sortedByDescending { it.finishedAt }
        .map { game -> HistoryItem(game, isPersonalBest = bestByDifficulty[game.difficulty] == game.id) }
        .groupBy { sectionKeyFor(it.game.finishedAt.toLocalDate(zone), today, weekFields) }
        .map { (key, items) -> HistorySection(key, items) }
}

/** One record holder per difficulty: the fastest time, and on a tie the one set first. */
private fun personalBests(games: List<GameSummary>): Map<Difficulty, GameId> =
    games
        .filter { it.status == GameStatus.COMPLETED && it.duration != null }
        .groupBy { it.difficulty }
        .mapValues { (_, completed) ->
            completed.minWith(compareBy({ it.duration!! }, { it.finishedAt })).id
        }

private fun sectionKeyFor(date: LocalDate, today: LocalDate, weekFields: WeekFields): HistorySectionKey =
    when {
        !date.isBefore(today) -> HistorySectionKey.Today
        date == today.minusDays(1) -> HistorySectionKey.Yesterday
        isSameWeek(date, today, weekFields) -> HistorySectionKey.ThisWeek
        else -> HistorySectionKey.Month(YearMonth.from(date))
    }

private fun isSameWeek(a: LocalDate, b: LocalDate, weekFields: WeekFields): Boolean =
    a.get(weekFields.weekBasedYear()) == b.get(weekFields.weekBasedYear()) &&
        a.get(weekFields.weekOfWeekBasedYear()) == b.get(weekFields.weekOfWeekBasedYear())

private fun Instant.toLocalDate(zone: ZoneId): LocalDate =
    java.time.Instant.ofEpochMilli(toEpochMilliseconds()).atZone(zone).toLocalDate()
