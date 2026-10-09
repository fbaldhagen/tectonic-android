package dev.fbaldhagen.tectonic.feature.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeAction
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeEvent
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeViewModel

@Composable
fun HomeScreen(
    onContinueClick: () -> Unit,
    onGameStarted: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnGameStarted by rememberUpdatedState(onGameStarted)

    // Runs on every return to Home, so a game finished or abandoned elsewhere is reflected.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onAction(HomeAction.Refresh)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                HomeEvent.NavigateToPuzzle -> currentOnGameStarted()
            }
        }
    }

    HomeContent(
        state = state,
        onAction = viewModel::onAction,
        onContinueClick = onContinueClick,
        onHistoryClick = onHistoryClick,
        onStatisticsClick = onStatisticsClick,
        onSettingsClick = onSettingsClick,
        modifier = modifier,
    )
}
