package dev.fbaldhagen.tectonic.feature.history.presentation

import dev.fbaldhagen.tectonic.ui.text.UiText

data class HistoryUiState(
    val isLoading: Boolean = true,
    val sections: List<HistorySection> = emptyList(),
    val error: UiText? = null,
)

sealed interface HistoryAction {
    data object Retry : HistoryAction
}
