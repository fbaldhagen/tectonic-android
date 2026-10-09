package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.GameSummary
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.ui.format.toClockString
import dev.fbaldhagen.tectonic.ui.format.toDisplayDate
import dev.fbaldhagen.tectonic.ui.text.toUiText

@Composable
internal fun HistoryGameRow(
    game: GameSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompleted = game.status == GameStatus.COMPLETED

    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(
                imageVector = if (isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel,
                contentDescription = stringResource(
                    if (isCompleted) R.string.history_completed_icon else R.string.history_abandoned_icon,
                ),
                tint = if (isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        },
        headlineContent = {
            Text(
                stringResource(
                    R.string.history_row_title,
                    game.difficulty.toUiText().asString(),
                    game.gridSize.toUiText().asString(),
                ),
            )
        },
        supportingContent = { Text(game.finishedAt.toDisplayDate()) },
        trailingContent = {
            val duration = game.duration
            if (isCompleted && duration != null) {
                Text(text = duration.toClockString(), style = MaterialTheme.typography.titleMedium)
            } else {
                Text(
                    text = game.status.toUiText().asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}
