package dev.fbaldhagen.tectonic.feature.statistics.presentation

import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GameStatistics
import dev.fbaldhagen.tectonic.ui.text.UiText

data class StatisticsUiState(
    val isLoading: Boolean = true,
    /** Null means all difficulties. */
    val selectedDifficulty: Difficulty? = null,
    val statistics: GameStatistics? = null,
    val error: UiText? = null,
)

sealed interface StatisticsAction {
    data class SelectDifficulty(val difficulty: Difficulty?) : StatisticsAction
    data object Retry : StatisticsAction
}
