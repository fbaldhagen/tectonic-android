package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleCellUiModel
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleGridUiModel

@Composable
fun PuzzleBoard(
    grid: PuzzleGridUiModel,
    selectedCell: CellId?,
    incorrectCells: Set<CellId>,
    modifier: Modifier = Modifier,
    onCellClick: (CellId) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(grid.columns),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(
            items = grid.cells,
            key = { it.cellId.row * grid.columns + it.cellId.column },
        ) { cell ->
            PuzzleBoardCell(
                cell = cell,
                grid = grid,
                isSelected = cell.cellId == selectedCell,
                isIncorrect = cell.cellId in incorrectCells,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable(enabled = !cell.isGiven) {
                        onCellClick(cell.cellId)
                    },
            )
        }
    }
}

@Composable
private fun PuzzleBoardCell(
    cell: PuzzleCellUiModel,
    grid: PuzzleGridUiModel,
    isSelected: Boolean,
    isIncorrect: Boolean,
    modifier: Modifier,
) {
    val background = when {
        isIncorrect -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        cell.isGiven -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val lineColor = MaterialTheme.colorScheme.outline
    val strongLineColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .background(background)
            .drawBehind {
                val left = cell.cellId.column == 0 || !sameRegion(grid, cell, cell.cellId.row, cell.cellId.column - 1)
                val right = cell.cellId.column == grid.columns - 1 || !sameRegion(grid, cell, cell.cellId.row, cell.cellId.column + 1)
                val top = cell.cellId.row == 0 || !sameRegion(grid, cell, cell.cellId.row - 1, cell.cellId.column)
                val bottom = cell.cellId.row == grid.rows - 1 || !sameRegion(grid, cell, cell.cellId.row + 1, cell.cellId.column)
                val normalWidth = 1.dp.toPx()
                val strongWidth = 2.dp.toPx()

                drawRect(
                    color = lineColor,
                    style = Stroke(width = normalWidth),
                )
                if (left) {
                    drawLine(strongLineColor, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(0f, size.height), strokeWidth = strongWidth)
                }
                if (right) {
                    drawLine(strongLineColor, start = androidx.compose.ui.geometry.Offset(size.width, 0f), end = androidx.compose.ui.geometry.Offset(size.width, size.height), strokeWidth = strongWidth)
                }
                if (top) {
                    drawLine(strongLineColor, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(size.width, 0f), strokeWidth = strongWidth)
                }
                if (bottom) {
                    drawLine(strongLineColor, start = androidx.compose.ui.geometry.Offset(0f, size.height), end = androidx.compose.ui.geometry.Offset(size.width, size.height), strokeWidth = strongWidth)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        cell.value?.let { value ->
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = if (cell.isGiven) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private fun sameRegion(
    grid: PuzzleGridUiModel,
    source: PuzzleCellUiModel,
    row: Int,
    column: Int,
): Boolean {
    val neighbor = grid.cells.firstOrNull { it.cellId.row == row && it.cellId.column == column }
        ?: return false
    return neighbor.regionId == source.regionId
}
