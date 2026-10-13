package dev.fbaldhagen.tectonic.feature.history.presentation

import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.ui.board.TecBoardUiModel
import dev.fbaldhagen.tectonic.ui.text.UiText
import kotlin.time.Duration
import kotlin.time.Instant

data class GameReviewUiModel(
    val board: TecBoardUiModel,
    val difficulty: Difficulty,
    val gridSize: GridSize,
    val status: GameStatus,
    val duration: Duration?,
    val date: Instant,
    /** Null when the game was not completed, or when no comparison could be made. */
    val comparison: TimeComparison?,
)

data class GameReviewUiState(
    val isLoading: Boolean = true,
    val review: GameReviewUiModel? = null,
    val error: UiText? = null,
    val isStartingGame: Boolean = false,
    val isAbandonDialogVisible: Boolean = false,
    /** A problem starting a new game, shown by the Play again button. */
    val playAgainError: UiText? = null,
)

sealed interface GameReviewAction {
    data class Load(val gameId: String) : GameReviewAction
    data object Retry : GameReviewAction
    data object PlayAgain : GameReviewAction
    data object AbandonConfirmed : GameReviewAction
    data object AbandonDismissed : GameReviewAction
}

sealed interface GameReviewEvent {
    data object NavigateToPuzzle : GameReviewEvent
}
