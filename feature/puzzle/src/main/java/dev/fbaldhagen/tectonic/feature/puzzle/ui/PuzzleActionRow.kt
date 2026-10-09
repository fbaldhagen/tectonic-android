package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.feature.puzzle.R

@Composable
internal fun PuzzleActionRow(
    canUndo: Boolean,
    canErase: Boolean,
    onUndoClick: () -> Unit,
    onEraseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilledTonalIconButton(
            onClick = onUndoClick,
            enabled = canUndo,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = stringResource(R.string.puzzle_undo),
            )
        }
        FilledTonalIconButton(
            onClick = onEraseClick,
            enabled = canErase,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = stringResource(R.string.puzzle_erase),
            )
        }
    }
}
