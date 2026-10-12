package dev.fbaldhagen.tectonic.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.ui.color.difficultyHue
import dev.fbaldhagen.tectonic.ui.text.toUiText

/**
 * One selectable difficulty. Each tier is tinted with its own region hue, so color
 * alone already separates them; selection adds a full-strength tint, an outline and a check.
 */
@Composable
internal fun HomeDifficultyCard(
    difficulty: Difficulty,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val hue = difficultyHue(difficulty)

    val tint by animateColorAsState(
        targetValue = if (isSelected) hue else hue.copy(alpha = hue.alpha * UNSELECTED_TINT),
        label = "difficultyTint",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary else colors.outline,
        label = "difficultyBorder",
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
        label = "difficultyBorderWidth",
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else UNSELECTED_SCALE,
        label = "difficultyScale",
    )

    val shape = MaterialTheme.shapes.large
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (isEnabled) 1f else DISABLED_ALPHA
            }
            .clip(shape)
            .background(colors.surface)
            .background(tint)
            .border(borderWidth, borderColor, shape)
            .selectable(
                selected = isSelected,
                enabled = isEnabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(vertical = 18.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = difficulty.gridSize.toUiText().asString(),
                style = MaterialTheme.typography.headlineMedium,
                color = colors.onSurface,
            )
            Text(
                text = difficulty.toUiText().asString(),
                style = MaterialTheme.typography.labelLarge,
                color = colors.onSurfaceVariant,
            )
        }

        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 2.dp, end = 2.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

private const val UNSELECTED_TINT = 0.55f
private const val UNSELECTED_SCALE = 0.97f
private const val DISABLED_ALPHA = 0.6f
