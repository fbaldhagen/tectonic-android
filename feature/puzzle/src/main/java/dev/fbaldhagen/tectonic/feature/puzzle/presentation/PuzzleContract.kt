package dev.fbaldhagen.tectonic.feature.puzzle.presentation

import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.MoveViolation
import dev.fbaldhagen.tectonic.ui.board.TecBoardUiModel
import kotlin.time.Duration

sealed interface PuzzleAction {
    data class SelectCell(val cellId: CellId) : PuzzleAction
    data class EnterValue(val value: Int) : PuzzleAction
    data object ClearCell : PuzzleAction
    data object Undo : PuzzleAction
    data class StartNewGame(val difficulty: Difficulty) : PuzzleAction
    data object DismissMessage : PuzzleAction
}

data class PuzzleMoveValidationUiModel(
    val isValid: Boolean,
    val violations: Set<MoveViolation>,
)

enum class PuzzleUiMessage {
    NO_ACTIVE_GAME,
    GENERIC_ERROR,
}

data class PuzzleUiState(
    val isLoading: Boolean = true,
    val gameId: String? = null,
    val difficulty: Difficulty? = null,
    val grid: TecBoardUiModel? = null,
    val isCompleted: Boolean = false,
    val completionTime: Duration? = null,
    val canUndo: Boolean = false,
    val selectedCell: CellId? = null,
    val moveValidation: PuzzleMoveValidationUiModel? = null,
    val message: PuzzleUiMessage? = null,
) {
    private val selectedBoardCell
        get() = selectedCell?.let { id -> grid?.cells?.firstOrNull { it.cellId == id } }

    /** Number of cells in the selected cell's region, which is also its highest allowed value. */
    val selectedRegionSize: Int?
        get() = selectedBoardCell?.let { selected ->
            grid?.cells?.count { it.regionId == selected.regionId }
        }

    val canErase: Boolean
        get() = !isCompleted && selectedBoardCell?.value != null
}
