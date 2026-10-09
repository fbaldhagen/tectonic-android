package dev.fbaldhagen.tectonic.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
internal fun TecBoardCell(
    cell: TecBoardCellUiModel,
    regions: BoardRegions,
    isSelected: Boolean,
    isIncorrect: Boolean,
    modifier: Modifier = Modifier,
) {
    val background = when {
        isIncorrect -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        cell.isGiven -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val lineColor = MaterialTheme.colorScheme.outline
    val regionLineColor = MaterialTheme.colorScheme.onSurface

    val row = cell.cellId.row
    val column = cell.cellId.column
    val region = cell.regionId.value
    val regionBorderLeft = !regions.isInRegion(row, column - 1, region)
    val regionBorderRight = !regions.isInRegion(row, column + 1, region)
    val regionBorderTop = !regions.isInRegion(row - 1, column, region)
    val regionBorderBottom = !regions.isInRegion(row + 1, column, region)

    Box(
        modifier = modifier
            .background(background)
            .drawBehind {
                val thin = 1.dp.toPx()
                val thick = 2.dp.toPx()

                drawRect(color = lineColor, style = Stroke(width = thin))
                if (regionBorderLeft) {
                    drawLine(regionLineColor, Offset(0f, 0f), Offset(0f, size.height), thick)
                }
                if (regionBorderRight) {
                    drawLine(regionLineColor, Offset(size.width, 0f), Offset(size.width, size.height), thick)
                }
                if (regionBorderTop) {
                    drawLine(regionLineColor, Offset(0f, 0f), Offset(size.width, 0f), thick)
                }
                if (regionBorderBottom) {
                    drawLine(regionLineColor, Offset(0f, size.height), Offset(size.width, size.height), thick)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        cell.value?.let { value ->
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = if (cell.isGiven) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}
