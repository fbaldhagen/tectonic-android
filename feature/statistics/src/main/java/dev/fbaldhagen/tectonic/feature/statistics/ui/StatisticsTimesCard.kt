package dev.fbaldhagen.tectonic.feature.statistics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.DifficultyTimes
import dev.fbaldhagen.tectonic.feature.statistics.R
import dev.fbaldhagen.tectonic.ui.color.difficultyHue
import dev.fbaldhagen.tectonic.ui.format.toClockString
import dev.fbaldhagen.tectonic.ui.text.toUiText

@Composable
internal fun StatisticsTimesCard(
    times: List<DifficultyTimes>,
    modifier: Modifier = Modifier,
) {
    StatisticsCard(
        title = stringResource(R.string.statistics_times_title),
        modifier = modifier,
    ) {
        Column {
            HeaderRow()
            times.forEach { entry ->
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                TimesRow(entry)
            }
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(NAME_WEIGHT))
        ColumnLabel(stringResource(R.string.statistics_times_best), Modifier.weight(1f))
        ColumnLabel(stringResource(R.string.statistics_times_average), Modifier.weight(1f))
    }
}

@Composable
private fun ColumnLabel(text: String, modifier: Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.End,
        modifier = modifier,
    )
}

@Composable
private fun TimesRow(entry: DifficultyTimes) {
    val none = stringResource(R.string.statistics_no_value)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(NAME_WEIGHT),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(difficultyHue(entry.difficulty)),
            )
            Column {
                Text(
                    text = entry.difficulty.toUiText().asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = entry.difficulty.gridSize.toUiText().asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        TimeValue(entry.best?.toClockString() ?: none, Modifier.weight(1f))
        TimeValue(entry.average?.toClockString() ?: none, Modifier.weight(1f))
    }
}

@Composable
private fun TimeValue(text: String, modifier: Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.End,
        modifier = modifier,
    )
}

private const val NAME_WEIGHT = 1.5f
