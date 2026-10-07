package dev.fbaldhagen.tectonic.feature.puzzle.presentation

import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.MoveViolation
import dev.fbaldhagen.tectonic.domain.model.RegionId
import dev.fbaldhagen.tectonic.domain.model.SolutionStatus

sealed interface PuzzleAction {
    data class SelectCell(val cellId: CellId) : PuzzleAction
    data class EnterValue(val value: Int) : PuzzleAction
    data object ClearCell : PuzzleAction
    data object Undo : PuzzleAction
    data object CheckSolution : PuzzleAction
    data class StartNewGame(val difficulty: Difficulty) : PuzzleAction
    data object DismissMessage : PuzzleAction
}

data class PuzzleCellUiModel(
    val cellId: CellId,
    val regionId: RegionId,
    val value: Int?,
    val isGiven: Boolean,
)

data class PuzzleGridUiModel(
    val rows: Int,
    val columns: Int,
    val cells: List<PuzzleCellUiModel>,
)

data class PuzzleMoveValidationUiModel(
    val isValid: Boolean,
    val violations: Set<MoveViolation>,
)

data class PuzzleSolutionCheckUiModel(
    val status: SolutionStatus,
    val incorrectCells: Set<CellId>,
)

enum class PuzzleUiMessage {
    NO_ACTIVE_GAME,
    GENERIC_ERROR,
}

data class PuzzleUiState(
    val isLoading: Boolean = true,
    val gameId: String? = null,
    val difficulty: Difficulty? = null,
    val grid: PuzzleGridUiModel? = null,
    val selectedCell: CellId? = null,
    val moveValidation: PuzzleMoveValidationUiModel? = null,
    val solutionCheck: PuzzleSolutionCheckUiModel? = null,
    val message: PuzzleUiMessage? = null,
)