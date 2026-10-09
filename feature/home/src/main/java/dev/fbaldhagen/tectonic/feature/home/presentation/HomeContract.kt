package dev.fbaldhagen.tectonic.feature.home.presentation

import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.ui.text.UiText

data class HomeInProgressGameUiModel(
    val gameId: String,
    val difficulty: Difficulty,
    val gridSize: GridSize,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val inProgressGame: HomeInProgressGameUiModel? = null,
    val selectedDifficulty: Difficulty = Difficulty.EASY,
    val isStartingGame: Boolean = false,
    val isAbandonDialogVisible: Boolean = false,
    val error: UiText? = null,
)

sealed interface HomeAction {
    data object Refresh : HomeAction
    data class SelectDifficulty(val difficulty: Difficulty) : HomeAction
    data object StartNewGame : HomeAction
    data object NewGameRequested : HomeAction
    data object AbandonConfirmed : HomeAction
    data object AbandonDismissed : HomeAction
}

sealed interface HomeEvent {
    data object NavigateToPuzzle : HomeEvent
}
