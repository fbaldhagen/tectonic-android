package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.feature.history.presentation.HistoryItem
import dev.fbaldhagen.tectonic.ui.format.toClockString
import dev.fbaldhagen.tectonic.ui.format.toDisplayDate
import dev.fbaldhagen.tectonic.ui.text.toUiText

@Composable
internal fun HistoryGameRow(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val game = item.game
    val colors = MaterialTheme.colorScheme
    val duration = game.duration
    val isCompleted = game.status == GameStatus.COMPLETED && duration != null

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = colors.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HistoryGridBadge(difficulty = game.difficulty, isMuted = !isCompleted)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.difficulty.toUiText().asString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                )
                Text(
                    text = game.finishedAt.toDisplayDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }

            if (isCompleted) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = duration.toClockString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.onSurface,
                    )
                    if (item.isPersonalBest) HistoryBestBadge()
                }
            } else {
                Text(
                    text = game.status.toUiText().asString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}
