package dev.fbaldhagen.tectonic.ui.state

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult

/**
 * Screen/section-level state that adds a [Loading] case on top of [TecResult],
 * which only models already-resolved outcomes. Typical use: a ViewModel
 * exposes `StateFlow<UiState<T>>`, starting at [Loading].
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: AppError) : UiState<Nothing>
}

/** Maps a resolved [TecResult] onto [UiState] - there's no in-between Loading case here. */
fun <T> TecResult<T>.toUiState(): UiState<T> = when (this) {
    is TecResult.Success -> UiState.Success(data)
    is TecResult.Error -> UiState.Error(error)
}