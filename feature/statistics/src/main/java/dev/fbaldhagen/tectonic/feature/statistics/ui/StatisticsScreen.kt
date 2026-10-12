package dev.fbaldhagen.tectonic.feature.statistics.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.fbaldhagen.tectonic.feature.statistics.presentation.StatisticsViewModel

@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    StatisticsContent(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onNavigateBack,
        modifier = modifier,
    )
}
