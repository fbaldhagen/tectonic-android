package dev.fbaldhagen.tectonic.domain.repository

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameSummary

interface GameHistoryRepository {

    /** The game the player can continue, or null when none is in progress. */
    suspend fun getInProgressGame(): TecResult<Game?>

    /** Completed and abandoned games, most recently finished first. */
    suspend fun getFinishedGameSummaries(): TecResult<List<GameSummary>>
}