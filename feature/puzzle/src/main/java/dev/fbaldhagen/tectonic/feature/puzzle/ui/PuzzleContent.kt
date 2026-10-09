package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleAction
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleUiState

@Composable
fun PuzzleContent(
    state: PuzzleUiState,
    onAction: (PuzzleAction) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        val board = state.grid

        when {
            state.isLoading -> PuzzleLoading(modifier = contentModifier)

            board != null -> PuzzleGame(
                state = state,
                board = board,
                onAction = onAction,
                onNavigateHome = onNavigateHome,
                onNavigateToHistory = onNavigateToHistory,
                modifier = contentModifier,
            )

            state.message != null -> PuzzleMessage(
                message = state.message,
                onDismiss = { onAction(PuzzleAction.DismissMessage) },
                onNewGame = { onAction(PuzzleAction.StartNewGame(Difficulty.EASY)) },
                onNavigateHome = onNavigateHome,
                modifier = contentModifier,
            )
        }
    }
}