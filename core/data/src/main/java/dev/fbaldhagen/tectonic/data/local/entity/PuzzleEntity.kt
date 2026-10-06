package dev.fbaldhagen.tectonic.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puzzles")
data class PuzzleEntity(
    @PrimaryKey val id: String,
    val rows: Int,
    val columns: Int,
    val difficulty: String,          // Difficulty.name
    val cellsJson: String,           // List<Cell>
    val regionsJson: String,         // List<Region>
    val solutionJson: String,        // Map<CellId, Int>
    val createdAt: Long = System.currentTimeMillis()
)