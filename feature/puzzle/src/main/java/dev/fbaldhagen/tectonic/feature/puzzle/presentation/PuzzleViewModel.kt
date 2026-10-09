package dev.fbaldhagen.tectonic.feature.puzzle.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.SolutionCheckResult
import dev.fbaldhagen.tectonic.domain.usecase.CheckSolutionUseCase
import dev.fbaldhagen.tectonic.domain.usecase.ClearCellUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetCurrentGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.MakeMoveUseCase
import dev.fbaldhagen.tectonic.domain.usecase.StartNewGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.UndoMoveUseCase
import dev.fbaldhagen.tectonic.domain.usecase.ValidateMoveUseCase
import dev.fbaldhagen.tectonic.ui.board.toBoardUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val getCurrentGameUseCase: GetCurrentGameUseCase,
    private val startNewGameUseCase: StartNewGameUseCase,
    private val validateMoveUseCase: ValidateMoveUseCase,
    private val makeMoveUseCase: MakeMoveUseCase,
    private val clearCellUseCase: ClearCellUseCase,
    private val undoMoveUseCase: UndoMoveUseCase,
    private val checkSolutionUseCase: CheckSolutionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleUiState())
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    init {
        loadCurrentGame()
    }

    fun onAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.SelectCell -> selectCell(action.cellId)
            is PuzzleAction.EnterValue -> enterValue(action.value)
            PuzzleAction.ClearCell -> clearCell()
            PuzzleAction.Undo -> undo()
            PuzzleAction.CheckSolution -> checkSolution()
            is PuzzleAction.StartNewGame -> startNewGame(action.difficulty)
            PuzzleAction.DismissMessage -> dismissMessage()
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
                        showGame(game)
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

                    when (val moveResult = makeMoveUseCase(GameId(gameId), cellId, value)) {
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
            when (val result = clearCellUseCase(GameId(gameId), cellId)) {
                is TecResult.Success -> showGame(result.data)
                is TecResult.Error -> showGenericError()
            }
        }
    }

    private fun undo() {
        val gameId = _uiState.value.gameId ?: return

        viewModelScope.launch {
            when (val result = undoMoveUseCase(GameId(gameId))) {
                is TecResult.Success -> showGame(result.data)
                is TecResult.Error -> showGenericError()
            }
        }
    }

    private fun checkSolution() {
        val gameId = _uiState.value.gameId ?: return

        viewModelScope.launch {
            when (val result = checkSolutionUseCase(GameId(gameId))) {
                is TecResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            solutionCheck = result.data.toUiModel(),
                            message = null,
                        )
                    }
                }

                is TecResult.Error -> showGenericError()
            }
        }
    }

    private fun startNewGame(difficulty: Difficulty) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            when (val result = startNewGameUseCase(difficulty)) {
                is TecResult.Success -> showGame(result.data)

                is TecResult.Error -> {
                    showGenericError()
                }
            }
        }
    }

    private fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun showGame(game: Game) {
        val isCompleted = game.status == GameStatus.COMPLETED
        _uiState.update {
            it.copy(
                isLoading = false,
                gameId = game.id.value,
                difficulty = game.puzzle.difficulty,
                grid = game.toBoardUiModel(),
                isCompleted = isCompleted,
                selectedCell = if (isCompleted) null else it.selectedCell,
                moveValidation = null,
                solutionCheck = null,
                message = null,
            )
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

    private fun SolutionCheckResult.toUiModel(): PuzzleSolutionCheckUiModel =
        PuzzleSolutionCheckUiModel(
            status = status,
            incorrectCells = incorrectCells,
        )
}