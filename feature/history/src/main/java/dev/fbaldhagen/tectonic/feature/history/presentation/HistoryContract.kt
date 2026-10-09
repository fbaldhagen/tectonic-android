package dev.fbaldhagen.tectonic.feature.history.presentation

import dev.fbaldhagen.tectonic.domain.model.GameSummary
import dev.fbaldhagen.tectonic.ui.text.UiText

data class HistoryUiState(
    val isLoading: Boolean = true,
    val games: List<GameSummary> = emptyList(),
    val error: UiText? = null,
)

sealed interface HistoryAction {
    data object Retry : HistoryAction
}
