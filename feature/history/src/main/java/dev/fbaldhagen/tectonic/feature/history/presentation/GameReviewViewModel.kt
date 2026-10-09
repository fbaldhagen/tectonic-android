package dev.fbaldhagen.tectonic.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.usecase.GetGameUseCase
import dev.fbaldhagen.tectonic.ui.board.toBoardUiModel
import dev.fbaldhagen.tectonic.ui.text.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameReviewViewModel @Inject constructor(
    private val getGameUseCase: GetGameUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameReviewUiState())
    val uiState: StateFlow<GameReviewUiState> = _uiState.asStateFlow()

    private var requestedGameId: String? = null

    fun onAction(action: GameReviewAction) {
        when (action) {
            is GameReviewAction.Load -> {
                // Load is dispatched from composition, so skip repeats for the same game.
                if (action.gameId != requestedGameId) load(action.gameId)
            }

            GameReviewAction.Retry -> requestedGameId?.let(::load)
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
                        showError(gameId, AppError.Unknown("Game not found: $gameId"))
                    } else {
                        _uiState.update { it.copy(isLoading = false, review = game.toReviewUiModel()) }
                    }
                }

                is TecResult.Error -> showError(gameId, result.error)
            }
        }
    }

    private fun showError(gameId: String, error: AppError) {
        _uiState.update { it.copy(isLoading = false, error = error.toUiText()) }
    }

    private fun Game.toReviewUiModel() = GameReviewUiModel(
        board = toBoardUiModel(),
        difficulty = puzzle.difficulty,
        gridSize = puzzle.gridSize,
        status = status,
        duration = result?.duration,
        date = result?.completedAt ?: startedAt,
    )
}
