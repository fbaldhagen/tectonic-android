package dev.fbaldhagen.tectonic.feature.history.presentation

import dev.fbaldhagen.tectonic.domain.model.DifficultyTimes
import kotlin.time.Duration

/** How a completed game's time compares with the player's other games of the same difficulty. */
sealed interface TimeComparison {

    /** The only completed game of its difficulty, so there is nothing to compare with. */
    data object FirstOfDifficulty : TimeComparison

    /**
     * @property deltaToBest zero or more; zero means this game ties or sets the best time
     * @property deltaToAverage negative when faster than the average, positive when slower
     */
    data class Compared(
        val isPersonalBest: Boolean,
        val deltaToBest: Duration,
        val deltaToAverage: Duration,
    ) : TimeComparison
}

/** [times] is expected to include [duration] itself, as statistics over all completed games do. */
internal fun compareTimes(duration: Duration, times: DifficultyTimes): TimeComparison? {
    val best = times.best ?: return null
    val average = times.average ?: return null
    if (times.completed <= 1) return TimeComparison.FirstOfDifficulty

    return TimeComparison.Compared(
        isPersonalBest = duration <= best,
        deltaToBest = maxOf(duration - best, Duration.ZERO),
        deltaToAverage = duration - average,
    )
}
