package dev.fbaldhagen.tectonic.ui.board

import dev.fbaldhagen.tectonic.domain.model.Game

fun Game.toBoardUiModel(): TecBoardUiModel =
    TecBoardUiModel(
        rows = puzzle.gridSize.rows,
        columns = puzzle.gridSize.columns,
        cells = puzzle.cells
            .sortedWith(compareBy({ it.id.row }, { it.id.column }))
            .map { cell ->
                TecBoardCellUiModel(
                    cellId = cell.id,
                    regionId = cell.regionId,
                    value = state.values[cell.id],
                    isGiven = cell.givenValue != null,
                )
            },
    )
