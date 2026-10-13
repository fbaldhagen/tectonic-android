package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.fbaldhagen.tectonic.feature.history.R

@Composable
internal fun GameReviewAbandonDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.review_abandon_title)) },
        text = { Text(stringResource(R.string.review_abandon_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.review_abandon_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.review_abandon_cancel))
            }
        },
    )
}
