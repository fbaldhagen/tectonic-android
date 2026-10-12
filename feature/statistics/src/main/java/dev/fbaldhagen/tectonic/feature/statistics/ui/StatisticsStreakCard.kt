package dev.fbaldhagen.tectonic.feature.statistics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.feature.statistics.R

@Composable
internal fun StatisticsStreakCard(
    currentDays: Int,
    longestDays: Int,
    modifier: Modifier = Modifier,
) {
    StatisticsCard(
        title = stringResource(R.string.statistics_streaks_title),
        modifier = modifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StreakTile(
                icon = Icons.Filled.LocalFireDepartment,
                days = currentDays,
                label = stringResource(R.string.statistics_streak_current),
                isHighlighted = currentDays > 0,
                modifier = Modifier.weight(1f),
            )
            StreakTile(
                icon = Icons.Filled.EmojiEvents,
                days = longestDays,
                label = stringResource(R.string.statistics_streak_longest),
                isHighlighted = longestDays > 0,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StreakTile(
    icon: ImageVector,
    days: Int,
    label: String,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = colors.surfaceVariant.copy(alpha = 0.55f),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isHighlighted) colors.primaryContainer else colors.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isHighlighted) colors.onPrimaryContainer else colors.onSurfaceVariant,
                )
            }
            Column {
                Text(
                    text = pluralStringResource(R.plurals.statistics_days, days, days),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}
