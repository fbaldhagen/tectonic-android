package dev.fbaldhagen.tectonic.feature.statistics.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.ui.format.toClockString
import kotlin.time.Duration

/**
 * Line chart of solve times, oldest to newest. The vertical axis runs from the fastest to the
 * slowest time shown, so the shape of the trend is visible rather than the absolute scale.
 */
@Composable
internal fun SolveTimeChart(
    times: List<Duration>,
    lineColor: Color,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    if (times.isEmpty()) return

    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val surface = MaterialTheme.colorScheme.surface
    val fastest = times.min()
    val slowest = times.max()

    Row(
        modifier = modifier
            .height(CHART_HEIGHT)
            .semantics { this.contentDescription = contentDescription },
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            AxisLabel(slowest.toClockString())
            AxisLabel(fastest.toClockString())
        }

        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            val inset = 10.dp.toPx()
            val right = size.width - inset
            val bottom = size.height - inset
            val spanMs = (slowest - fastest).inWholeMilliseconds

            fun x(index: Int): Float =
                if (times.size == 1) (inset + right) / 2f
                else inset + (right - inset) * index / (times.size - 1)

            fun y(time: Duration): Float =
                if (spanMs == 0L) (inset + bottom) / 2f
                else bottom - (bottom - inset) * ((time - fastest).inWholeMilliseconds.toFloat() / spanMs)

            listOf(inset, (inset + bottom) / 2f, bottom).forEach { lineY ->
                drawLine(gridColor, Offset(0f, lineY), Offset(size.width, lineY), strokeWidth = 1.dp.toPx())
            }

            if (times.size > 1) {
                val line = Path().apply {
                    times.forEachIndexed { index, time ->
                        if (index == 0) moveTo(x(index), y(time)) else lineTo(x(index), y(time))
                    }
                }
                val area = Path().apply {
                    addPath(line)
                    lineTo(x(times.lastIndex), bottom)
                    lineTo(x(0), bottom)
                    close()
                }
                drawPath(
                    path = area,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.28f), Color.Transparent),
                        startY = inset,
                        endY = bottom,
                    ),
                )
                drawPath(
                    path = line,
                    color = lineColor,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }

            times.forEachIndexed { index, time ->
                drawCircle(lineColor, radius = 3.5.dp.toPx(), center = Offset(x(index), y(time)))
            }

            // Ring the most recent result.
            val last = Offset(x(times.lastIndex), y(times.last()))
            drawCircle(lineColor, radius = 7.dp.toPx(), center = last)
            drawCircle(surface, radius = 3.5.dp.toPx(), center = last)
        }
    }
}

@Composable
private fun AxisLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private val CHART_HEIGHT = 150.dp
