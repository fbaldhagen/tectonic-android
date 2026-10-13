package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleAction
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleUiState
import dev.fbaldhagen.tectonic.ui.board.TecBoard
import dev.fbaldhagen.tectonic.ui.board.TecBoardUiModel

/**
 * Header on top, board centered in the free space, controls anchored at the bottom
 * where the thumbs are.
 */
@Composable
internal fun PuzzleGame(
    state: PuzzleUiState,
    board: TecBoardUiModel,
    onAction: (PuzzleAction) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        PuzzleHeader(
            difficulty = state.difficulty,
            elapsed = state.elapsed,
            onBackClick = onNavigateHome,
            onNewGameClick = { onAction(PuzzleAction.StartNewGame(it)) },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            TecBoard(
                board = board,
                selectedCell = state.selectedCell,
                onCellClick = if (state.isCompleted) null else { cellId ->
                    onAction(PuzzleAction.SelectCell(cellId))
                },
                modifier = Modifier.widthIn(max = 480.dp),
            )
        }

        PuzzleControls(
            state = state,
            onAction = onAction,
            modifier = Modifier
                .widthIn(max = 480.dp)
                .align(Alignment.CenterHorizontally)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        )
    }

    if (state.isCompleted) {
        PuzzleCompletedDialog(
            time = state.completionTime,
            onHomeClick = onNavigateHome,
            onHistoryClick = onNavigateToHistory,
        )
    }
}
