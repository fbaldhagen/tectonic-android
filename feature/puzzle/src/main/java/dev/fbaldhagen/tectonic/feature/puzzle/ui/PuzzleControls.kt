package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.feature.puzzle.R
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleAction
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleUiState

/** Bottom-anchored input area: rule feedback, undo and erase, then the digit keys. */
@Composable
internal fun PuzzleControls(
    state: PuzzleUiState,
    onAction: (PuzzleAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Fixed height so showing or hiding the message never moves the keys.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (state.moveValidation?.isValid == false) {
                Text(
                    text = stringResource(R.string.puzzle_invalid_move),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        PuzzleActionRow(
            canUndo = state.canUndo && !state.isCompleted,
            canErase = state.canErase,
            onUndoClick = { onAction(PuzzleAction.Undo) },
            onEraseClick = { onAction(PuzzleAction.ClearCell) },
        )

        PuzzleDigitRow(
            selectedRegionSize = state.selectedRegionSize,
            isEnabled = !state.isCompleted,
            onDigitClick = { onAction(PuzzleAction.EnterValue(it)) },
        )
    }
}
