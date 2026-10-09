package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Always shows the same digits in the same places. Digits larger than the selected
 * region are disabled instead of hidden, so the layout never shifts between cells.
 */
@Composable
internal fun PuzzleDigitRow(
    selectedRegionSize: Int?,
    isEnabled: Boolean,
    onDigitClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (digit in 1..MAX_DIGIT) {
            FilledTonalButton(
                onClick = { onDigitClick(digit) },
                enabled = isEnabled && selectedRegionSize != null && digit <= selectedRegionSize,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
            ) {
                Text(text = digit.toString(), style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

/** Tectonic regions hold at most five cells, so the keyboard never needs more keys. */
private const val MAX_DIGIT = 5
