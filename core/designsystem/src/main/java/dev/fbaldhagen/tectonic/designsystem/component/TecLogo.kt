package dev.fbaldhagen.tectonic.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Brand mark: four tiles in a 2x2 grid where the right column has slipped down,
 * like plates along a fault line. Colors follow the active theme.
 */
@Composable
fun TecLogo(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = modifier.size(size)) {
        val side = this.size.minDimension
        val tile = side * TILE_RATIO
        val gap = side * GAP_RATIO
        val slip = side * SLIP_RATIO
        val originX = (side - (2 * tile + gap)) / 2f
        val originY = (side - (2 * tile + gap + slip)) / 2f
        val corner = CornerRadius(tile * CORNER_RATIO)

        fun plate(column: Int, row: Int, color: Color) {
            drawRoundRect(
                color = color,
                topLeft = Offset(
                    x = originX + column * (tile + gap),
                    y = originY + row * (tile + gap) + if (column == 1) slip else 0f,
                ),
                size = Size(tile, tile),
                cornerRadius = corner,
            )
        }

        plate(column = 0, row = 0, color = colors.primary)
        plate(column = 1, row = 0, color = colors.secondary)
        plate(column = 0, row = 1, color = colors.tertiary)
        plate(column = 1, row = 1, color = colors.primaryContainer)
    }
}

private const val TILE_RATIO = 0.40f
private const val GAP_RATIO = 0.06f
private const val SLIP_RATIO = 0.10f
private const val CORNER_RATIO = 0.22f
