package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.component.TecEmptyState
import dev.fbaldhagen.tectonic.designsystem.component.TecErrorState
import dev.fbaldhagen.tectonic.designsystem.component.TecLoadingState
import dev.fbaldhagen.tectonic.designsystem.component.TecScreenHeader
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.HistoryAction
import dev.fbaldhagen.tectonic.feature.history.presentation.HistorySection
import dev.fbaldhagen.tectonic.feature.history.presentation.HistoryUiState

@Composable
fun HistoryContent(
    state: HistoryUiState,
    onAction: (HistoryAction) -> Unit,
    onBackClick: () -> Unit,
    onGameClick: (GameId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TecScreenHeader(
                title = stringResource(R.string.history_title),
                onBackClick = onBackClick,
                backContentDescription = stringResource(R.string.history_back),
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    state.isLoading -> TecLoadingState()

                    state.error != null -> TecErrorState(
                        message = state.error.asString(),
                        onRetryClick = { onAction(HistoryAction.Retry) },
                    )

                    state.sections.isEmpty() -> TecEmptyState(
                        title = stringResource(R.string.history_empty_title),
                        description = stringResource(R.string.history_empty_description),
                        icon = Icons.Outlined.History,
                    )

                    else -> HistoryList(state.sections, onGameClick)
                }
            }
        }
    }
}

@Composable
private fun HistoryList(
    sections: List<HistorySection>,
    onGameClick: (GameId) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        sections.forEach { section ->
            stickyHeader(key = section.id) {
                HistorySectionHeader(title = section.key.title())
            }
            items(items = section.items, key = { it.game.id.value }) { item ->
                HistoryGameRow(item = item, onClick = { onGameClick(item.game.id) })
            }
        }
    }
}
