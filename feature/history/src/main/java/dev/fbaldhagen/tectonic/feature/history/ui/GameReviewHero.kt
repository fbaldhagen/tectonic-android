package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewUiModel
import dev.fbaldhagen.tectonic.ui.color.difficultyHue
import dev.fbaldhagen.tectonic.ui.format.toClockString
import dev.fbaldhagen.tectonic.ui.text.toUiText

/** The result at a glance: status, a large time, and how it compares with earlier games. */
@Composable
internal fun GameReviewHero(
    review: GameReviewUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val duration = review.duration
    val isCompleted = review.status == GameStatus.COMPLETED && duration != null
    val hue = difficultyHue(review.difficulty)
    val glow = hue.alpha * if (isCompleted) COMPLETED_GLOW else ABANDONED_GLOW

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = colors.surface,
        shadowElevation = 8.dp,
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(listOf(hue.copy(alpha = glow), Color.Transparent)),
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatusPill(status = review.status)

                if (isCompleted) {
                    Text(
                        text = stringResource(R.string.review_your_time),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onSurfaceVariant,
                    )
                    Text(
                        text = duration.toClockString(),
                        // Tabular figures keep the digits from shifting as the value changes.
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 56.sp,
                            fontFeatureSettings = "tnum",
                        ),
                        color = colors.onSurface,
                    )
                    review.comparison?.let {
                        GameReviewComparison(comparison = it, difficulty = review.difficulty)
                    }
                } else {
                    Text(
                        text = stringResource(R.string.review_not_finished),
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: GameStatus) {
    val colors = MaterialTheme.colorScheme
    val isCompleted = status == GameStatus.COMPLETED

    Surface(
        shape = CircleShape,
        color = if (isCompleted) colors.primaryContainer else colors.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = if (isCompleted) colors.onPrimaryContainer else colors.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = status.toUiText().asString(),
                style = MaterialTheme.typography.labelLarge,
                color = if (isCompleted) colors.onPrimaryContainer else colors.onSurfaceVariant,
            )
        }
    }
}

private const val COMPLETED_GLOW = 0.8f
private const val ABANDONED_GLOW = 0.3f
