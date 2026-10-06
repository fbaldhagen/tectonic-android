package dev.fbaldhagen.tectonic.data.local.entity

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
    val updatedAt: Long = System.currentTimeMillis()
)