package dev.fbaldhagen.tectonic.ui.board

import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.RegionId

data class TecBoardCellUiModel(
    val cellId: CellId,
    val regionId: RegionId,
    val value: Int?,
    val isGiven: Boolean,
)

data class TecBoardUiModel(
    val rows: Int,
    val columns: Int,
    val cells: List<TecBoardCellUiModel>,
)
