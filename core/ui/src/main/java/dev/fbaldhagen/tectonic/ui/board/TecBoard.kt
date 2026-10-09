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
import dev.fbaldhagen.tectonic.domain.model.CellId

/**
 * Renders a puzzle grid with region borders. Pass [onCellClick] to make non-given cells
 * tappable; leave it null for a read-only board.
 */
@Composable
fun TecBoard(
    board: TecBoardUiModel,
    modifier: Modifier = Modifier,
    selectedCell: CellId? = null,
    incorrectCells: Set<CellId> = emptySet(),
    onCellClick: ((CellId) -> Unit)? = null,
) {
    val regions = remember(board) { BoardRegions.of(board) }

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
                isSelected = cell.cellId == selectedCell,
                isIncorrect = cell.cellId in incorrectCells,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable(enabled = onCellClick != null && !cell.isGiven) {
                        onCellClick?.invoke(cell.cellId)
                    },
            )
        }
    }
}
