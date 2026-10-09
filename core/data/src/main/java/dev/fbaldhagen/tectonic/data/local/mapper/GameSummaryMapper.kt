package dev.fbaldhagen.tectonic.data.local.mapper

import dev.fbaldhagen.tectonic.data.local.converter.TectonicTypeConverters
import dev.fbaldhagen.tectonic.data.local.projection.GameSummaryProjection
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.GameSummary
import dev.fbaldhagen.tectonic.domain.model.GridSize
import kotlin.time.Instant

private val converters = TectonicTypeConverters()

fun GameSummaryProjection.toDomain(): GameSummary {
    val result = converters.jsonToGameResult(resultJson)

    return GameSummary(
        id = GameId(gameId),
        difficulty = Difficulty.valueOf(difficulty),
        gridSize = GridSize(rows = gridRows, columns = gridColumns),
        status = GameStatus.valueOf(status),
        duration = result?.duration,
        finishedAt = result?.completedAt ?: Instant.fromEpochMilliseconds(updatedAt)
    )
}
