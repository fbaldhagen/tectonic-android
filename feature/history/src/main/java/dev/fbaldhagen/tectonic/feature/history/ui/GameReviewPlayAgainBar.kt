package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.component.TecPrimaryButton
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.ui.text.UiText

/** Pinned under the scrolling content, so the main action is always in reach. */
@Composable
internal fun GameReviewPlayAgainBar(
    isStarting: Boolean,
    error: UiText?,
    onPlayAgainClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(modifier = Modifier.widthIn(max = 440.dp)) {
            error?.let {
                Text(
                    text = it.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            TecPrimaryButton(
                text = stringResource(
                    if (isStarting) R.string.review_generating else R.string.review_play_again,
                ),
                onClick = onPlayAgainClick,
                isLoading = isStarting,
                leadingIcon = if (isStarting) null else Icons.Filled.Replay,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
