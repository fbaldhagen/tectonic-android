package dev.fbaldhagen.tectonic.feature.puzzle.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.usecase.ClearCellUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetCurrentGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.MakeMoveUseCase
import dev.fbaldhagen.tectonic.domain.usecase.SaveElapsedTimeUseCase
import dev.fbaldhagen.tectonic.domain.usecase.StartNewGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.UndoMoveUseCase
import dev.fbaldhagen.tectonic.domain.usecase.ValidateMoveUseCase
import dev.fbaldhagen.tectonic.ui.board.toBoardUiModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val getCurrentGameUseCase: GetCurrentGameUseCase,
    private val startNewGameUseCase: StartNewGameUseCase,
    private val validateMoveUseCase: ValidateMoveUseCase,
    private val makeMoveUseCase: MakeMoveUseCase,
    private val clearCellUseCase: ClearCellUseCase,
    private val undoMoveUseCase: UndoMoveUseCase,
    private val saveElapsedTimeUseCase: SaveElapsedTimeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleUiState())
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    private val clock = ActivePlayClock()
    private var tickerJob: Job? = null
    private var isScreenResumed = false

    init {
        loadCurrentGame()
    }

    fun onAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.SelectCell -> selectCell(action.cellId)
            is PuzzleAction.EnterValue -> enterValue(action.value)
            PuzzleAction.ClearCell -> clearCell()
            PuzzleAction.Undo -> undo()
            is PuzzleAction.StartNewGame -> startNewGame(action.difficulty)
            PuzzleAction.DismissMessage -> dismissMessage()
            PuzzleAction.ScreenResumed -> onScreenResumed()
            PuzzleAction.ScreenPaused -> onScreenPaused()
        }
    }

    private fun loadCurrentGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            when (val result = getCurrentGameUseCase()) {
                is TecResult.Success -> {
                    val game = result.data
                    if (game == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                message = PuzzleUiMessage.NO_ACTIVE_GAME,
                            )
                        }
                    } else {
                        showGame(game, resetClock = true)
                    }
                }

                is TecResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = PuzzleUiMessage.GENERIC_ERROR,
                        )
                    }
                }
            }
        }
    }

    private fun selectCell(cellId: CellId) {
        if (_uiState.value.isCompleted) return
        val grid = _uiState.value.grid ?: return
        val cell = grid.cells.firstOrNull { it.cellId == cellId } ?: return
        if (cell.isGiven) return

        _uiState.update {
            it.copy(
                selectedCell = cellId,
                moveValidation = null,
            )
        }
    }

    private fun enterValue(value: Int) {
        val gameId = _uiState.value.gameId ?: return
        val cellId = _uiState.value.selectedCell ?: return

        viewModelScope.launch {
            when (val validationResult = validateMoveUseCase(GameId(gameId), cellId, value)) {
                is TecResult.Success -> {
                    val validation = validationResult.data
                    if (!validation.isValid) {
                        _uiState.update {
                            it.copy(
                                moveValidation = PuzzleMoveValidationUiModel(
                                    isValid = false,
                                    violations = validation.violations,
                                ),
                            )
                        }
                        return@launch
                    }

                    when (val moveResult = makeMoveUseCase(GameId(gameId), cellId, value, clock.elapsed())) {
                        is TecResult.Success -> {
                            showGame(moveResult.data)
                        }

                        is TecResult.Error -> showGenericError()
                    }
                }

                is TecResult.Error -> showGenericError()
            }
        }
    }

    private fun clearCell() {
        val gameId = _uiState.value.gameId ?: return
        val cellId = _uiState.value.selectedCell ?: return

        viewModelScope.launch {
            when (val result = clearCellUseCase(GameId(gameId), cellId, clock.elapsed())) {
                is TecResult.Success -> showGame(result.data)
                is TecResult.Error -> showGenericError()
            }
        }
    }

    private fun undo() {
        val gameId = _uiState.value.gameId ?: return

        viewModelScope.launch {
            when (val result = undoMoveUseCase(GameId(gameId), clock.elapsed())) {
                is TecResult.Success -> showGame(result.data)
                is TecResult.Error -> showGenericError()
            }
        }
    }

    private fun startNewGame(difficulty: Difficulty) {
        // The game being left keeps the time played so far.
        if (clock.isRunning) {
            stopClock()
            persistElapsed()
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            when (val result = startNewGameUseCase(difficulty)) {
                is TecResult.Success -> showGame(result.data, resetClock = true)

                is TecResult.Error -> {
                    Log.e("PuzzleViewModel", "StartNewGame failed: ${result.error}")
                    showGenericError()
                }
            }
        }
    }

    private fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    /**
     * @param resetClock true when this is a different game than the one on screen, so the clock
     * restarts from the saved time. Results of moves keep the running clock, which is the
     * source of truth while the screen is open.
     */
    private fun showGame(game: Game, resetClock: Boolean = false) {
        val isCompleted = game.status == GameStatus.COMPLETED

        if (resetClock) {
            stopClock()
            clock.reset(game.elapsed)
        }
        if (isCompleted) stopClock()

        _uiState.update {
            it.copy(
                isLoading = false,
                gameId = game.id.value,
                difficulty = game.puzzle.difficulty,
                grid = game.toBoardUiModel(),
                isCompleted = isCompleted,
                elapsed = if (isCompleted) game.result?.duration ?: game.elapsed else clock.elapsed(),
                completionTime = game.result?.duration,
                canUndo = game.state.moves.isNotEmpty(),
                selectedCell = if (isCompleted) null else it.selectedCell,
                moveValidation = null,
                message = null,
            )
        }

        if (resetClock) startClockIfPlayable()
    }

    private fun onScreenResumed() {
        isScreenResumed = true
        startClockIfPlayable()
    }

    private fun onScreenPaused() {
        isScreenResumed = false
        if (!clock.isRunning) return

        stopClock()
        persistElapsed()
    }

    private fun startClockIfPlayable() {
        val state = _uiState.value
        if (!isScreenResumed || state.gameId == null || state.isCompleted || clock.isRunning) return

        clock.start()
        tickerJob = viewModelScope.launch {
            var ticksSinceSave = 0
            while (isActive) {
                // Wake on whole seconds of play time so the display never lags or skips.
                delay((TICK_MILLIS - clock.elapsed().inWholeMilliseconds % TICK_MILLIS).milliseconds)
                _uiState.update { it.copy(elapsed = clock.elapsed()) }

                if (++ticksSinceSave >= SAVE_EVERY_TICKS) {
                    ticksSinceSave = 0
                    persistElapsed()
                }
            }
        }
    }

    private fun stopClock() {
        clock.stop()
        tickerJob?.cancel()
        tickerJob = null
        _uiState.update { it.copy(elapsed = clock.elapsed()) }
    }

    /**
     * Saves the clock without touching the board. Started undispatched and shielded from
     * cancellation, so the final write still lands when leaving the screen clears this
     * view model right afterward.
     */
    private fun persistElapsed() {
        val gameId = _uiState.value.gameId ?: return
        val elapsed = clock.elapsed()

        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            withContext(NonCancellable) {
                val result = saveElapsedTimeUseCase(GameId(gameId), elapsed)
                if (result is TecResult.Error) {
                    Log.e("PuzzleViewMode","Saving elapsed time failed: gameId=$gameId ${result.error}")
                }
            }
        }
    }

    private fun showGenericError() {
        _uiState.update {
            it.copy(
                isLoading = false,
                message = PuzzleUiMessage.GENERIC_ERROR,
            )
        }
    }

    private companion object {
        const val TICK_MILLIS = 1_000L
        const val SAVE_EVERY_TICKS = 15
    }
}
