package dev.fbaldhagen.tectonic.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.usecase.AbandonGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetInProgressGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.StartNewGameUseCase
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
class HomeViewModel @Inject constructor(
    private val getInProgressGameUseCase: GetInProgressGameUseCase,
    private val startNewGameUseCase: StartNewGameUseCase,
    private val abandonGameUseCase: AbandonGameUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.Refresh -> refresh()
            is HomeAction.SelectDifficulty -> _uiState.update { it.copy(selectedDifficulty = action.difficulty) }
            HomeAction.StartNewGame -> startNewGame()
            HomeAction.NewGameRequested -> _uiState.update { it.copy(isAbandonDialogVisible = true) }
            HomeAction.AbandonConfirmed -> abandonCurrentGame()
            HomeAction.AbandonDismissed -> _uiState.update { it.copy(isAbandonDialogVisible = false) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            when (val result = getInProgressGameUseCase()) {
                is TecResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        inProgressGame = result.data?.toHomeUiModel(),
                        error = null,
                    )
                }

                is TecResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }
        }
    }

    private fun startNewGame() {
        val current = _uiState.value
        if (current.isStartingGame || current.inProgressGame != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isStartingGame = true, error = null) }

            // Puzzle generation is CPU-bound, so keep it off the main thread.
            val result = withContext(Dispatchers.Default) {
                startNewGameUseCase(current.selectedDifficulty)
            }

            when (result) {
                is TecResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isStartingGame = false,
                            inProgressGame = result.data.toHomeUiModel(),
                        )
                    }
                    _events.send(HomeEvent.NavigateToPuzzle)
                }

                is TecResult.Error -> {
                    _uiState.update {
                        it.copy(isStartingGame = false, error = result.error.toUiText())
                    }
                }
            }
        }
    }

    private fun abandonCurrentGame() {
        val game = _uiState.value.inProgressGame
        _uiState.update { it.copy(isAbandonDialogVisible = false) }
        if (game == null) return

        viewModelScope.launch {
            when (val result = abandonGameUseCase(GameId(game.gameId))) {
                is TecResult.Success -> _uiState.update { it.copy(inProgressGame = null, error = null) }

                is TecResult.Error -> {
                    _uiState.update { it.copy(error = result.error.toUiText()) }
                    refresh()
                }
            }
        }
    }
}
