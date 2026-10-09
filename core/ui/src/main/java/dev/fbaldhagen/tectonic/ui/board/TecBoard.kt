package dev.fbaldhagen.tectonic.ui.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.domain.model.CellId

/**
 * Renders a puzzle grid with region borders and per-region background colors.
 * Pass [onCellClick] to make non-given cells tappable; leave it null for a read-only board.
 */
@Composable
fun TecBoard(
    board: TecBoardUiModel,
    modifier: Modifier = Modifier,
    selectedCell: CellId? = null,
    onCellClick: ((CellId) -> Unit)? = null,
) {
    val regions = remember(board) { BoardRegions.of(board) }
    val palette = TecTheme.extendedColors.regionHues
    val colorIndexByRegion = remember(board, palette.size) {
        assignRegionColors(board, paletteSize = maxOf(palette.size, 1))
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(board.columns),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(
            items = board.cells,
            key = { it.cellId.row * board.columns + it.cellId.column },
        ) { cell ->
            TecBoardCell(
                cell = cell,
                regions = regions,
                regionColor = palette.getOrElse(colorIndexByRegion.getValue(cell.regionId)) { Color.Transparent },
                isSelected = cell.cellId == selectedCell,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable(enabled = onCellClick != null && !cell.isGiven) {
                        onCellClick?.invoke(cell.cellId)
                    },
            )
        }
    }
}
