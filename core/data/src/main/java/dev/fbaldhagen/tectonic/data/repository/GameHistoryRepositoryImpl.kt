package dev.fbaldhagen.tectonic.data.repository

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.common.suspendRunCatching
import dev.fbaldhagen.tectonic.data.local.dao.GameDao
import dev.fbaldhagen.tectonic.data.local.dao.PuzzleDao
import dev.fbaldhagen.tectonic.data.local.mapper.toDomain
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.GameSummary
import dev.fbaldhagen.tectonic.domain.repository.GameHistoryRepository
import javax.inject.Inject

class GameHistoryRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val puzzleDao: PuzzleDao
) : GameHistoryRepository {

    override suspend fun getInProgressGame(): TecResult<Game?> {
        return suspendRunCatching {
            val gameEntity = gameDao.getMostRecentWithStatus(GameStatus.IN_PROGRESS.name)
                ?: return@suspendRunCatching null

            val puzzleEntity = puzzleDao.getById(gameEntity.puzzleId)
                ?: throw NoSuchElementException(
                    "Puzzle for game not found: ${gameEntity.puzzleId}"
                )

            gameEntity.toDomain(puzzleEntity.toDomain())
        }
    }

    override suspend fun getFinishedGameSummaries(): TecResult<List<GameSummary>> {
        return suspendRunCatching {
            gameDao
                .getSummaries(listOf(GameStatus.COMPLETED.name, GameStatus.ABANDONED.name))
                .map { it.toDomain() }
        }
    }
}