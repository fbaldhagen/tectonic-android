package dev.fbaldhagen.tectonic.feature.statistics.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.feature.statistics.R
import dev.fbaldhagen.tectonic.ui.format.toClockString
import kotlin.time.Duration

/**
 * Times from different grid sizes are not comparable, so the chart only appears once a
 * single difficulty is selected.
 */
@Composable
internal fun StatisticsTrendCard(
    difficulty: Difficulty?,
    recentSolveTimes: List<Duration>,
    modifier: Modifier = Modifier,
) {
    StatisticsCard(
        title = stringResource(R.string.statistics_trend_title),
        modifier = modifier,
    ) {
        when {
            difficulty == null -> HintText(stringResource(R.string.statistics_trend_pick_difficulty))

            recentSolveTimes.isEmpty() -> HintText(stringResource(R.string.statistics_trend_empty))

            else -> {
                SolveTimeChart(
                    times = recentSolveTimes,
                    lineColor = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(
                        R.string.statistics_trend_description,
                        recentSolveTimes.size,
                        recentSolveTimes.last().toClockString(),
                    ),
                )
                Text(
                    text = stringResource(R.string.statistics_trend_caption, recentSolveTimes.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HintText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
