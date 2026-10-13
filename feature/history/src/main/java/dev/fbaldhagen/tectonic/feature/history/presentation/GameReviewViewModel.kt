package dev.fbaldhagen.tectonic.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.usecase.AbandonGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetInProgressGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetStatisticsUseCase
import dev.fbaldhagen.tectonic.domain.usecase.StartNewGameUseCase
import dev.fbaldhagen.tectonic.ui.board.toBoardUiModel
import dev.fbaldhagen.tectonic.ui.text.toUiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GameReviewViewModel @Inject constructor(
    private val getGameUseCase: GetGameUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getInProgressGameUseCase: GetInProgressGameUseCase,
    private val abandonGameUseCase: AbandonGameUseCase,
    private val startNewGameUseCase: StartNewGameUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameReviewUiState())
    val uiState: StateFlow<GameReviewUiState> = _uiState.asStateFlow()

    private val _events = Channel<GameReviewEvent>(Channel.BUFFERED)
    val events: Flow<GameReviewEvent> = _events.receiveAsFlow()

    private var requestedGameId: String? = null

    /** The game that would be abandoned if the player confirms Play again. */
    private var pendingInProgressId: GameId? = null

    fun onAction(action: GameReviewAction) {
        when (action) {
            is GameReviewAction.Load -> {
                // Load is dispatched from composition, so skip repeats for the same game.
                if (action.gameId != requestedGameId) load(action.gameId)
            }

            GameReviewAction.Retry -> requestedGameId?.let(::load)
            GameReviewAction.PlayAgain -> playAgain()
            GameReviewAction.AbandonConfirmed -> abandonThenStart()
            GameReviewAction.AbandonDismissed -> {
                pendingInProgressId = null
                _uiState.update { it.copy(isAbandonDialogVisible = false) }
            }
        }
    }

    private fun load(gameId: String) {
        requestedGameId = gameId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getGameUseCase(GameId(gameId))) {
                is TecResult.Success -> {
                    val game = result.data
                    if (game == null) {
                        showError(AppError.Unknown("Game not found: $gameId"))
                    } else {
                        val review = game.toReviewUiModel(comparison = comparisonFor(game))
                        _uiState.update { it.copy(isLoading = false, review = review) }
                    }
                }

                is TecResult.Error -> showError(result.error)
            }
        }
    }

    /** A missing comparison is not worth failing the screen for, so errors just omit it. */
    private suspend fun comparisonFor(game: Game): TimeComparison? {
        val duration = game.result?.duration
        if (game.status != GameStatus.COMPLETED || duration == null) return null

        return when (val stats = getStatisticsUseCase(game.puzzle.difficulty)) {
            is TecResult.Success -> stats.data.times
                .firstOrNull { it.difficulty == game.puzzle.difficulty }
                ?.let { compareTimes(duration, it) }

            is TecResult.Error -> {
                null
            }
        }
    }

    private fun showError(error: AppError) {
        _uiState.update { it.copy(isLoading = false, error = error.toUiText()) }
    }

    private fun playAgain() {
        val difficulty = _uiState.value.review?.difficulty ?: return
        if (_uiState.value.isStartingGame) return

        viewModelScope.launch {
            _uiState.update { it.copy(playAgainError = null) }

            when (val inProgress = getInProgressGameUseCase()) {
                is TecResult.Success -> {
                    val game = inProgress.data
                    if (game == null) {
                        startGame(difficulty)
                    } else {
                        // Starting a game would orphan this one, so ask before abandoning it.
                        pendingInProgressId = game.id
                        _uiState.update { it.copy(isAbandonDialogVisible = true) }
                    }
                }

                is TecResult.Error -> showPlayAgainError(inProgress.error)
            }
        }
    }

    private fun abandonThenStart() {
        val difficulty = _uiState.value.review?.difficulty
        val toAbandon = pendingInProgressId
        pendingInProgressId = null
        _uiState.update { it.copy(isAbandonDialogVisible = false) }
        if (difficulty == null || toAbandon == null) return

        viewModelScope.launch {
            when (val abandoned = abandonGameUseCase(toAbandon)) {
                is TecResult.Success -> startGame(difficulty)
                is TecResult.Error -> showPlayAgainError(abandoned.error)
            }
        }
    }

    private suspend fun startGame(difficulty: Difficulty) {
        _uiState.update { it.copy(isStartingGame = true, playAgainError = null) }

        // Puzzle generation is CPU-bound, so keep it off the main thread.
        val result = withContext(Dispatchers.Default) { startNewGameUseCase(difficulty) }

        when (result) {
            is TecResult.Success -> {
                _uiState.update { it.copy(isStartingGame = false) }
                _events.send(GameReviewEvent.NavigateToPuzzle)
            }

            is TecResult.Error -> {
                _uiState.update { it.copy(isStartingGame = false) }
                showPlayAgainError(result.error)
            }
        }
    }

    private fun showPlayAgainError(error: AppError) {
        _uiState.update { it.copy(playAgainError = error.toUiText()) }
    }

    private fun Game.toReviewUiModel(comparison: TimeComparison?) = GameReviewUiModel(
        board = toBoardUiModel(),
        difficulty = puzzle.difficulty,
        gridSize = puzzle.gridSize,
        status = status,
        duration = result?.duration,
        date = result?.completedAt ?: startedAt,
        comparison = comparison,
    )
}
