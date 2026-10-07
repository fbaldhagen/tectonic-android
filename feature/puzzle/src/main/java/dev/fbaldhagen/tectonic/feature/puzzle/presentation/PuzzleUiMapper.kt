package dev.fbaldhagen.tectonic.feature.puzzle.presentation

import dev.fbaldhagen.tectonic.domain.model.Game

internal fun Game.toPuzzleUiGrid(): PuzzleGridUiModel =
    PuzzleGridUiModel(
        rows = puzzle.gridSize.rows,
        columns = puzzle.gridSize.columns,
        cells = puzzle.cells
            .sortedWith(compareBy({ it.id.row }, { it.id.column }))
            .map { cell ->
            PuzzleCellUiModel(
                cellId = cell.id,
                regionId = cell.regionId,
                value = state.values[cell.id],
                isGiven = cell.givenValue != null,
            )
        },
    )
