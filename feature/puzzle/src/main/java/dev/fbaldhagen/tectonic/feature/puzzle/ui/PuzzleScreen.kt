package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleViewModel

@Composable
fun PuzzleScreen(
    onNavigateHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: PuzzleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PuzzleContent(
        state = uiState,
        onAction = viewModel::onAction,
        onNavigateHome = onNavigateHome,
        onNavigateToHistory = onNavigateToHistory,
    )
}
