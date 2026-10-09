package dev.fbaldhagen.tectonic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.fbaldhagen.tectonic.data.local.entity.GameEntity
import dev.fbaldhagen.tectonic.data.local.projection.GameSummaryProjection

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getById(id: String): GameEntity?

    @Query("SELECT * FROM games ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getMostRecent(): GameEntity?

    @Query("SELECT * FROM games WHERE status = :status ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getMostRecentWithStatus(status: String): GameEntity?

    // `rows` and `columns` are SQL keywords, so they are quoted and aliased to other names.
    @Query(
        """
        SELECT games.id AS gameId,
               games.status AS status,
               games.resultJson AS resultJson,
               games.updatedAt AS updatedAt,
               puzzles.difficulty AS difficulty,
               puzzles.`rows` AS gridRows,
               puzzles.`columns` AS gridColumns
        FROM games
        INNER JOIN puzzles ON puzzles.id = games.puzzleId
        WHERE games.status IN (:statuses)
        ORDER BY games.updatedAt DESC
        """
    )
    suspend fun getSummaries(statuses: List<String>): List<GameSummaryProjection>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GameEntity)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteById(id: String)
}
