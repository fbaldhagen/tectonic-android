package dev.fbaldhagen.tectonic.data.local.mapper

import dev.fbaldhagen.tectonic.data.local.converter.TectonicTypeConverters
import dev.fbaldhagen.tectonic.data.local.entity.PuzzleEntity
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleId

private val converters = TectonicTypeConverters()

fun PuzzleEntity.toDomain(): Puzzle {
    val cells = converters.jsonToCells(cellsJson)
    val regions = converters.jsonToRegions(regionsJson)
    val solution = converters.jsonToSolution(solutionJson)

    return Puzzle(
        id = PuzzleId(id),
        gridSize = GridSize(rows, columns),
        cells = cells,
        regions = regions,
        solution = solution,
        difficulty = Difficulty.valueOf(difficulty)
    )
}

fun Puzzle.toEntity(): PuzzleEntity {
    return PuzzleEntity(
        id = id.value,
        rows = gridSize.rows,
        columns = gridSize.columns,
        difficulty = difficulty.name,
        cellsJson = converters.cellsToJson(cells),
        regionsJson = converters.regionsToJson(regions),
        solutionJson = converters.solutionToJson(solution)
    )
}