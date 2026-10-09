package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.feature.puzzle.R
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleUiMessage

/** Temporary fallback shown when there is no game to display. */
@Composable
internal fun PuzzleMessage(
    message: PuzzleUiMessage,
    onDismiss: () -> Unit,
    onNewGame: () -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val text = when (message) {
            PuzzleUiMessage.NO_ACTIVE_GAME -> stringResource(R.string.puzzle_no_active_game)
            PuzzleUiMessage.GENERIC_ERROR -> stringResource(R.string.puzzle_generic_error)
        }
        Text(text = text, style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onNewGame) {
                Text(stringResource(R.string.puzzle_start_easy))
            }
            OutlinedButton(onClick = onNavigateHome) {
                Text(stringResource(R.string.puzzle_home))
            }
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.puzzle_dismiss))
            }
        }
    }
}
