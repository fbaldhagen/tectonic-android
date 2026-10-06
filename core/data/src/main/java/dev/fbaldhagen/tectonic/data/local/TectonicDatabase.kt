package dev.fbaldhagen.tectonic.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.fbaldhagen.tectonic.data.local.converter.TectonicTypeConverters
import dev.fbaldhagen.tectonic.data.local.dao.GameDao
import dev.fbaldhagen.tectonic.data.local.dao.PuzzleDao
import dev.fbaldhagen.tectonic.data.local.entity.GameEntity
import dev.fbaldhagen.tectonic.data.local.entity.PuzzleEntity

@Database(
    entities = [PuzzleEntity::class, GameEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(TectonicTypeConverters::class)
abstract class TectonicDatabase : RoomDatabase() {
    abstract fun puzzleDao(): PuzzleDao
    abstract fun gameDao(): GameDao
}