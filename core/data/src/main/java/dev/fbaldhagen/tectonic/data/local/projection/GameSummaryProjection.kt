package dev.fbaldhagen.tectonic.data.local.projection

/**
 * Result shape of the history query: game and puzzle metadata without any board JSON.
 * Not a table, so it is not an entity; the mapper turns it into the domain GameSummary.
 */
data class GameSummaryProjection(
    val gameId: String,
    val status: String,
    val resultJson: String?,
    val updatedAt: Long,
    val difficulty: String,
    val gridRows: Int,
    val gridColumns: Int
)
