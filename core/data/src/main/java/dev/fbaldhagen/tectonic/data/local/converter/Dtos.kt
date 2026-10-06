package dev.fbaldhagen.tectonic.data.local.converter

import kotlinx.serialization.Serializable

@Serializable
data class CellIdDto(val row: Int, val column: Int)

@Serializable
data class CellDto(
    val row: Int,
    val column: Int,
    val regionId: Int,
    val givenValue: Int?
)

@Serializable
data class RegionDto(
    val id: Int,
    val cells: List<CellIdDto>
)

@Serializable
data class MoveDto(
    val cellRow: Int,
    val cellColumn: Int,
    val previousValue: Int?,
    val newValue: Int?
)

@Serializable
data class GameResultDto(
    val completedAtEpochMs: Long,
    val durationMs: Long,
    val mistakes: Int,
    val hintsUsed: Int
)