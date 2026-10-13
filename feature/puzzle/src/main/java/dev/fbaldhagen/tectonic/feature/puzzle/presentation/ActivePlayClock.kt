package dev.fbaldhagen.tectonic.feature.puzzle.presentation

import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Accumulates active play time: it only advances between [start] and [stop].
 *
 * Uses a monotonic time source, so changes to the wall clock or time zone cannot affect it.
 * Not thread-safe; use it from a single thread such as the main thread.
 */
internal class ActivePlayClock(
    private val timeSource: TimeSource = TimeSource.Monotonic,
) {
    private var accumulated: Duration = Duration.ZERO
    private var runningSince: TimeMark? = null

    val isRunning: Boolean get() = runningSince != null

    /** Replaces the accumulated time and stops the clock. */
    fun reset(elapsed: Duration) {
        accumulated = elapsed
        runningSince = null
    }

    /** Starts counting; does nothing if already running. */
    fun start() {
        if (runningSince == null) runningSince = timeSource.markNow()
    }

    fun stop() {
        runningSince?.let { accumulated += it.elapsedNow() }
        runningSince = null
    }

    fun elapsed(): Duration = accumulated + (runningSince?.elapsedNow() ?: Duration.ZERO)
}
