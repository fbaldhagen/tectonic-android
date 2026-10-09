package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.fbaldhagen.tectonic.feature.history.presentation.HistoryViewModel

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onGameClick: (gameId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HistoryContent(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onNavigateBack,
        onGameClick = { onGameClick(it.value) },
        modifier = modifier,
    )
}
