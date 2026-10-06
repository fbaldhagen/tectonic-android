package dev.fbaldhagen.tectonic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.fbaldhagen.tectonic.data.local.entity.PuzzleEntity

@Dao
interface PuzzleDao {

    @Query("SELECT * FROM puzzles WHERE id = :id")
    suspend fun getById(id: String): PuzzleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PuzzleEntity)

    @Query("DELETE FROM puzzles WHERE id = :id")
    suspend fun deleteById(id: String)
}