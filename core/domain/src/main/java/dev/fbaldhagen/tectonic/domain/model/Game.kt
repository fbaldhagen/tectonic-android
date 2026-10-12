package dev.fbaldhagen.tectonic.domain.model

import kotlin.time.Duration
import kotlin.time.Instant

data class Game(
    val id: GameId,
    val puzzle: Puzzle,
    val state: PuzzleState,
    val status: GameStatus,
    val startedAt: Instant,
    val result: GameResult?,
    val elapsed: Duration = Duration.ZERO
)