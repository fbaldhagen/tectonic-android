package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewAction
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewViewModel

@Composable
fun GameReviewScreen(
    gameId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameReviewViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(gameId) {
        viewModel.onAction(GameReviewAction.Load(gameId))
    }

    GameReviewContent(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onNavigateBack,
        modifier = modifier,
    )
}
