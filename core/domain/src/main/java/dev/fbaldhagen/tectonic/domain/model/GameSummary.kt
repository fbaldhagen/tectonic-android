package dev.fbaldhagen.tectonic.domain.model

import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Lightweight view of a finished game for history lists. Carries no board data,
 * so listing many games does not load puzzles or move logs.
 */
data class GameSummary(
    val id: GameId,
    val difficulty: Difficulty,
    val gridSize: GridSize,
    val status: GameStatus,
    val duration: Duration?,
    val finishedAt: Instant
)