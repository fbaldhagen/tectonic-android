package dev.fbaldhagen.tectonic.domain.model

import kotlin.time.Duration

/** Solve times for one difficulty. Best and average are null until a game is completed. */
data class DifficultyTimes(
    val difficulty: Difficulty,
    val completed: Int,
    val best: Duration?,
    val average: Duration?
)

/**
 * Statistics over finished games in one scope (all difficulties, or a single one).
 *
 * @property completionRate completed divided by finished (completed plus abandoned), or null
 * when nothing has finished yet
 * @property totalTime summed wall-clock time of completed games
 * @property times one entry per difficulty in scope
 * @property recentSolveTimes the latest solve times, oldest first
 */
data class GameStatistics(
    val played: Int,
    val completed: Int,
    val abandoned: Int,
    val completionRate: Double?,
    val totalTime: Duration,
    val times: List<DifficultyTimes>,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val recentSolveTimes: List<Duration>
)
