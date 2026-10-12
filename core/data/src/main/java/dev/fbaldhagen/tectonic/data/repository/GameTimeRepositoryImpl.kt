package dev.fbaldhagen.tectonic.data.repository

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.common.suspendRunCatching
import dev.fbaldhagen.tectonic.data.local.dao.GameDao
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.repository.GameTimeRepository
import javax.inject.Inject
import kotlin.time.Duration

class GameTimeRepositoryImpl @Inject constructor(
    private val gameDao: GameDao
) : GameTimeRepository {

    override suspend fun saveElapsed(gameId: GameId, elapsed: Duration): TecResult<Unit> {
        return suspendRunCatching {
            gameDao.updateElapsed(gameId.value, elapsed.inWholeMilliseconds)
        }
    }
}
