package dev.fbaldhagen.tectonic.data.repository

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.common.suspendRunCatching
import dev.fbaldhagen.tectonic.data.local.dao.GameDao
import dev.fbaldhagen.tectonic.data.local.dao.PuzzleDao
import dev.fbaldhagen.tectonic.data.local.mapper.toDomain
import dev.fbaldhagen.tectonic.data.local.mapper.toEntity
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository
import javax.inject.Inject

class GameProgressRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val puzzleDao: PuzzleDao
) : GameProgressRepository {

    override suspend fun getGame(gameId: GameId): TecResult<Game> {
        return suspendRunCatching {
            val gameEntity = gameDao.getById(gameId.value)
                ?: throw NoSuchElementException("Game not found: ${gameId.value}")

            val puzzleEntity = puzzleDao.getById(gameEntity.puzzleId)
                ?: throw NoSuchElementException("Puzzle for game not found: ${gameEntity.puzzleId}")

            gameEntity.toDomain(puzzleEntity.toDomain())
        }
    }

    override suspend fun getMostRecentGame(): TecResult<Game?> {
        return suspendRunCatching {
            val gameEntity = gameDao.getMostRecent()
                ?: return@suspendRunCatching null

            val puzzleEntity = puzzleDao.getById(gameEntity.puzzleId)
                ?: throw NoSuchElementException(
                    "Puzzle for game not found: ${gameEntity.puzzleId}"
                )

            gameEntity.toDomain(puzzleEntity.toDomain())
        }
    }

    override suspend fun saveGame(game: Game): TecResult<Unit> {
        return suspendRunCatching {
            puzzleDao.insert(game.puzzle.toEntity())
            gameDao.insert(game.toEntity())
        }
    }

    override suspend fun deleteGame(gameId: GameId): TecResult<Unit> {
        return suspendRunCatching {
            gameDao.deleteById(gameId.value)
        }
    }
}