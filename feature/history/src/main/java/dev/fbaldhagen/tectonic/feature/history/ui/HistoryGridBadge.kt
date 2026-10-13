package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.ui.color.difficultyHue
import dev.fbaldhagen.tectonic.ui.text.toUiText

/** The grid size on a tile in the difficulty's color; muted when the game was not finished. */
@Composable
internal fun HistoryGridBadge(
    difficulty: Difficulty,
    isMuted: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val hue = difficultyHue(difficulty)

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(colors.surface)
            .background(if (isMuted) hue.copy(alpha = hue.alpha * MUTED_TINT) else hue),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = difficulty.gridSize.toUiText().asString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isMuted) colors.onSurfaceVariant else colors.onSurface,
        )
    }
}

private const val MUTED_TINT = 0.45f
