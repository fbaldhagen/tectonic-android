package dev.fbaldhagen.tectonic.data.local.mapper

import dev.fbaldhagen.tectonic.data.local.converter.TectonicTypeConverters
import dev.fbaldhagen.tectonic.data.local.entity.GameEntity
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleState
import kotlin.time.Instant

private val converters = TectonicTypeConverters()

fun GameEntity.toDomain(puzzle: Puzzle): Game {
    val values = converters.jsonToValues(valuesJson)
    val moves = converters.jsonToMoves(movesJson)
    val result = converters.jsonToGameResult(resultJson)

    return Game(
        id = GameId(id),
        puzzle = puzzle,
        state = PuzzleState(values = values, moves = moves),
        status = GameStatus.valueOf(status),
        startedAt = Instant.fromEpochMilliseconds(startedAtEpochMs),
        result = result
    )
}

fun Game.toEntity(): GameEntity {
    return GameEntity(
        id = id.value,
        puzzleId = puzzle.id.value,
        status = status.name,
        startedAtEpochMs = startedAt.toEpochMilliseconds(),
        valuesJson = converters.valuesToJson(state.values),
        movesJson = converters.movesToJson(state.moves),
        resultJson = converters.gameResultToJson(result)
    )
}