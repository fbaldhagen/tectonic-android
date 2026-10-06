package dev.fbaldhagen.tectonic.domain.model

import kotlin.time.Duration
import kotlin.time.Instant

data class GameResult(
    val completedAt: Instant,
    val duration: Duration,
    val mistakes: Int,
    val hintsUsed: Int
)