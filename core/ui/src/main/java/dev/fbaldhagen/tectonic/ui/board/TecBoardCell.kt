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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun TecBoardCell(
    cell: TecBoardCellUiModel,
    regions: BoardRegions,
    regionColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val surface = MaterialTheme.colorScheme.surface
    val selection = MaterialTheme.colorScheme.primary
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
            // Dark-theme hues are translucent, so they are laid over the surface color.
            .background(surface)
            .background(regionColor)
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

                if (isSelected) {
                    val inset = 3.dp.toPx()
                    drawRect(color = selection.copy(alpha = SELECTION_TINT_ALPHA))
                    drawRect(
                        color = selection,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - 2 * inset, size.height - 2 * inset),
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        cell.value?.let { value ->
            // Givens are bold and neutral; the player's own entries take the primary hue.
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (cell.isGiven) FontWeight.Bold else FontWeight.Medium,
                color = if (cell.isGiven) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )
        }
    }
}

private const val SELECTION_TINT_ALPHA = 0.22f
