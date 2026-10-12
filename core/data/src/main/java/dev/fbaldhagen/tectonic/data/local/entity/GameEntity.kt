package dev.fbaldhagen.tectonic.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val puzzleId: String,
    val status: String,              // GameStatus.name
    val startedAtEpochMs: Long,
    val valuesJson: String,          // Map<CellId, Int?>
    val movesJson: String,           // List<Move>
    val resultJson: String?,         // GameResult? (nullable)
    @ColumnInfo(defaultValue = "0")
    val elapsedMillis: Long = 0,     // active play time
    val updatedAt: Long = System.currentTimeMillis()
)