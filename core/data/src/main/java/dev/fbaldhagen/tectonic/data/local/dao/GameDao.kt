package dev.fbaldhagen.tectonic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.fbaldhagen.tectonic.data.local.entity.GameEntity

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getById(id: String): GameEntity?

    @Query("SELECT * FROM games ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getMostRecent(): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GameEntity)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteById(id: String)
}