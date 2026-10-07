package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.fbaldhagen.tectonic.feature.puzzle.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PuzzleNumberPad(
    maxValue: Int,
    onValueSelected: (Int) -> Unit,
    onClear: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (value in 1..maxValue) {
            Button(
                onClick = { onValueSelected(value) },
                modifier = Modifier.weight(1f),
            ) {
                Text(value.toString())
            }
        }
        OutlinedButton(
            onClick = onClear,
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(R.string.puzzle_clear))
        }
    }
}
