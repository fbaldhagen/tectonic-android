package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** A pill stating one fact about the result; [isHighlighted] marks the good news. */
@Composable
internal fun GameReviewDeltaChip(
    icon: ImageVector,
    text: String,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = if (isHighlighted) colors.primaryContainer else colors.surfaceVariant.copy(alpha = 0.7f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isHighlighted) colors.onPrimaryContainer else colors.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isHighlighted) colors.onPrimaryContainer else colors.onSurfaceVariant,
            )
        }
    }
}
