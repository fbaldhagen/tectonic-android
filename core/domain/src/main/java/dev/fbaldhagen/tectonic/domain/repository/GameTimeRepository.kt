package dev.fbaldhagen.tectonic.domain.repository

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.GameId
import kotlin.time.Duration

interface GameTimeRepository {

    /**
     * Records active play time for a game without touching its board or moves.
     * The stored value never decreases, so a late or repeated write cannot lose time.
     */
    suspend fun saveElapsed(gameId: GameId, elapsed: Duration): TecResult<Unit>
}
