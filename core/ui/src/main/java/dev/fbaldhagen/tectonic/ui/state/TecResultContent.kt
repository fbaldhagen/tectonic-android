package dev.fbaldhagen.tectonic.ui.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fbaldhagen.tectonic.designsystem.component.TecErrorState
import dev.fbaldhagen.tectonic.designsystem.component.TecLoadingState
import dev.fbaldhagen.tectonic.ui.text.toUiText

/**
 * Renders a [UiState] by delegating to `core:designsystem`'s dumb state
 * composables - this is the glue piece designsystem itself can't contain,
 * since it doesn't know [dev.fbaldhagen.tectonic.common.AppError] exists.
 *
 * Only [content] for the [UiState.Success] case is screen-specific; the
 * loading/error presentation is centralized here so every screen looks
 * consistent by default.
 */
@Composable
fun <T> TecResultContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    when (state) {
        is UiState.Loading -> TecLoadingState(modifier = modifier.fillMaxSize())

        is UiState.Error -> TecErrorState(
            message = state.error.toUiText().asString(),
            modifier = modifier.fillMaxSize(),
            onRetryClick = onRetry,
        )

        is UiState.Success -> Box(modifier = modifier) {
            content(state.data)
        }
    }
}