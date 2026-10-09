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
)

data class GameReviewUiState(
    val isLoading: Boolean = true,
    val review: GameReviewUiModel? = null,
    val error: UiText? = null,
)

sealed interface GameReviewAction {
    data class Load(val gameId: String) : GameReviewAction
    data object Retry : GameReviewAction
}
