package dev.fbaldhagen.tectonic.feature.statistics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.GameStatistics
import dev.fbaldhagen.tectonic.feature.statistics.R
import dev.fbaldhagen.tectonic.ui.format.toCompactString
import kotlin.math.roundToInt

@Composable
internal fun StatisticsOverviewCard(
    statistics: GameStatistics,
    modifier: Modifier = Modifier,
) {
    StatisticsCard(
        title = stringResource(R.string.statistics_overview_title),
        modifier = modifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatisticTile(
                value = statistics.played.toString(),
                label = stringResource(R.string.statistics_played),
                modifier = Modifier.weight(1f),
            )
            StatisticTile(
                value = statistics.completed.toString(),
                label = stringResource(R.string.statistics_completed),
                modifier = Modifier.weight(1f),
            )
            StatisticTile(
                value = statistics.abandoned.toString(),
                label = stringResource(R.string.statistics_abandoned),
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val rate = statistics.completionRate
            StatisticTile(
                value = rate?.let {
                    stringResource(R.string.statistics_percent, (it * 100).roundToInt())
                } ?: stringResource(R.string.statistics_no_value),
                label = stringResource(R.string.statistics_completion_rate),
                modifier = Modifier.weight(1f),
                extra = {
                    if (rate != null) {
                        LinearProgressIndicator(
                            progress = { rate.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .height(6.dp),
                        )
                    }
                },
            )
            StatisticTile(
                value = statistics.totalTime.toCompactString(),
                label = stringResource(R.string.statistics_total_time),
                modifier = Modifier.weight(1f),
            )
        }
    }
}
