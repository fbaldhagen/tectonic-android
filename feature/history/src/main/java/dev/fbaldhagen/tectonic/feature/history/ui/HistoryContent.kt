package dev.fbaldhagen.tectonic.feature.history.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.fbaldhagen.tectonic.designsystem.component.TecEmptyState
import dev.fbaldhagen.tectonic.designsystem.component.TecErrorState
import dev.fbaldhagen.tectonic.designsystem.component.TecLoadingState
import dev.fbaldhagen.tectonic.designsystem.component.TecScreenHeader
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.GameSummary
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.HistoryAction
import dev.fbaldhagen.tectonic.feature.history.presentation.HistoryUiState
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

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

                    state.games.isEmpty() -> TecEmptyState(
                        title = stringResource(R.string.history_empty_title),
                        description = stringResource(R.string.history_empty_description),
                        icon = Icons.Outlined.History,
                    )

                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = state.games, key = { it.id.value }) { game ->
                            HistoryGameRow(game = game, onClick = { onGameClick(game.id) })
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
    }
}

private val previewGames = listOf(
    GameSummary(
        id = GameId("1"),
        difficulty = Difficulty.HARD,
        gridSize = GridSize(8, 8),
        status = GameStatus.COMPLETED,
        duration = 12.minutes + 34.seconds,
        finishedAt = Instant.fromEpochSeconds(1_790_000_000),
    ),
    GameSummary(
        id = GameId("2"),
        difficulty = Difficulty.EASY,
        gridSize = GridSize(5, 5),
        status = GameStatus.ABANDONED,
        duration = null,
        finishedAt = Instant.fromEpochSeconds(1_789_900_000),
    ),
)

@Preview(showBackground = true)
@Composable
private fun HistoryContentPreview() {
    TecTheme {
        HistoryContent(
            state = HistoryUiState(isLoading = false, games = previewGames),
            onAction = {},
            onBackClick = {},
            onGameClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HistoryContentEmptyPreview() {
    TecTheme(darkTheme = true) {
        HistoryContent(
            state = HistoryUiState(isLoading = false),
            onAction = {},
            onBackClick = {},
            onGameClick = {},
        )
    }
}
