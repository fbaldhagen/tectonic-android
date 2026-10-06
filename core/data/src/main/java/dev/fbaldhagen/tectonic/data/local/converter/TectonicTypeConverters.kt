package dev.fbaldhagen.tectonic.data.local.converter

import androidx.room.TypeConverter
import dev.fbaldhagen.tectonic.domain.model.Cell
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.GameResult
import dev.fbaldhagen.tectonic.domain.model.Move
import dev.fbaldhagen.tectonic.domain.model.Region
import dev.fbaldhagen.tectonic.domain.model.RegionId
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class TectonicTypeConverters {

    // ---------- Cells ----------
    @TypeConverter
    fun cellsToJson(cells: List<Cell>): String {
        val dtos = cells.map {
            CellDto(it.id.row, it.id.column, it.regionId.value, it.givenValue)
        }
        return TectonicJson.encodeToString(dtos)
    }

    @TypeConverter
    fun jsonToCells(json: String): List<Cell> {
        val dtos = TectonicJson.decodeFromString<List<CellDto>>(json)
        return dtos.map {
            Cell(
                id = CellId(it.row, it.column),
                regionId = RegionId(it.regionId),
                givenValue = it.givenValue
            )
        }
    }

    // ---------- Regions ----------
    @TypeConverter
    fun regionsToJson(regions: List<Region>): String {
        val dtos = regions.map { r ->
            RegionDto(
                id = r.id.value,
                cells = r.cells.map { CellIdDto(it.row, it.column) }
            )
        }
        return TectonicJson.encodeToString(dtos)
    }

    @TypeConverter
    fun jsonToRegions(json: String): List<Region> {
        val dtos = TectonicJson.decodeFromString<List<RegionDto>>(json)
        return dtos.map { r ->
            Region(
                id = RegionId(r.id),
                cells = r.cells.map { CellId(it.row, it.column) }.toSet()
            )
        }
    }

    // ---------- Solution Map<CellId, Int> ----------
    @TypeConverter
    fun solutionToJson(solution: Map<CellId, Int>): String {
        val map = solution.entries.associate { (k, v) -> "${k.row},${k.column}" to v }
        return TectonicJson.encodeToString(map)
    }

    @TypeConverter
    fun jsonToSolution(json: String): Map<CellId, Int> {
        val map = TectonicJson.decodeFromString<Map<String, Int>>(json)
        return map.mapKeys { (key, _) ->
            val (r, c) = key.split(",")
            CellId(r.toInt(), c.toInt())
        }
    }

    // ---------- Values Map<CellId, Int?> ----------
    @TypeConverter
    fun valuesToJson(values: Map<CellId, Int?>): String {
        val map = values.entries.associate { (k, v) -> "${k.row},${k.column}" to v }
        return TectonicJson.encodeToString(map)
    }

    @TypeConverter
    fun jsonToValues(json: String): Map<CellId, Int?> {
        val map = TectonicJson.decodeFromString<Map<String, Int?>>(json)
        return map.mapKeys { (key, _) ->
            val (r, c) = key.split(",")
            CellId(r.toInt(), c.toInt())
        }
    }

    // ---------- Moves ----------
    @TypeConverter
    fun movesToJson(moves: List<Move>): String {
        val dtos = moves.map {
            MoveDto(it.cellId.row, it.cellId.column, it.previousValue, it.newValue)
        }
        return TectonicJson.encodeToString(dtos)
    }

    @TypeConverter
    fun jsonToMoves(json: String): List<Move> {
        val dtos = TectonicJson.decodeFromString<List<MoveDto>>(json)
        return dtos.map {
            Move(
                cellId = CellId(it.cellRow, it.cellColumn),
                previousValue = it.previousValue,
                newValue = it.newValue
            )
        }
    }

    // ---------- GameResult ----------
    @TypeConverter
    fun gameResultToJson(result: GameResult?): String? {
        if (result == null) return null
        val dto = GameResultDto(
            completedAtEpochMs = result.completedAt.toEpochMilliseconds(),
            durationMs = result.duration.inWholeMilliseconds,
            mistakes = result.mistakes,
            hintsUsed = result.hintsUsed
        )
        return TectonicJson.encodeToString(dto)
    }

    @TypeConverter
    fun jsonToGameResult(json: String?): GameResult? {
        if (json == null) return null
        val dto = TectonicJson.decodeFromString<GameResultDto>(json)
        return GameResult(
            completedAt = Instant.fromEpochMilliseconds(dto.completedAtEpochMs),
            duration = dto.durationMs.milliseconds,
            mistakes = dto.mistakes,
            hintsUsed = dto.hintsUsed
        )
    }
}