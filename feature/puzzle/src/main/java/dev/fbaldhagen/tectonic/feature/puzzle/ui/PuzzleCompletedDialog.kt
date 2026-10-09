package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import dev.fbaldhagen.tectonic.feature.puzzle.R
import dev.fbaldhagen.tectonic.ui.format.toClockString
import kotlin.time.Duration

/** Any way of dismissing this dialog, including system back, leads home. */
@Composable
internal fun PuzzleCompletedDialog(
    time: Duration?,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onHomeClick,
        properties = DialogProperties(dismissOnClickOutside = false),
        title = { Text(stringResource(R.string.puzzle_completed_title)) },
        text = time?.let {
            { Text(stringResource(R.string.puzzle_completed_time, it.toClockString())) }
        },
        confirmButton = {
            TextButton(onClick = onHomeClick) {
                Text(stringResource(R.string.puzzle_completed_home))
            }
        },
        dismissButton = {
            TextButton(onClick = onHistoryClick) {
                Text(stringResource(R.string.puzzle_completed_history))
            }
        },
    )
}
