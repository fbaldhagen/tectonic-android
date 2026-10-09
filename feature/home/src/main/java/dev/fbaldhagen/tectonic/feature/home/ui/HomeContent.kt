package dev.fbaldhagen.tectonic.feature.home.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.feature.home.R
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeAction
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeInProgressGameUiModel
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeUiState

@Composable
fun HomeContent(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onContinueClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
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
            HomeHeader(
                onStatisticsClick = onStatisticsClick,
                onSettingsClick = onSettingsClick,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                HomeBranding()
                Spacer(modifier = Modifier.height(48.dp))
                HomePrimaryActions(
                    state = state,
                    onAction = onAction,
                    onContinueClick = onContinueClick,
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = onHistoryClick) {
                    Icon(imageVector = Icons.Outlined.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.home_history))
                }
            }
        }
    }

    if (state.isAbandonDialogVisible) {
        HomeAbandonDialog(
            onConfirm = { onAction(HomeAction.AbandonConfirmed) },
            onDismiss = { onAction(HomeAction.AbandonDismissed) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentNewGamePreview() {
    TecTheme {
        HomeContent(
            state = HomeUiState(isLoading = false, selectedDifficulty = Difficulty.MEDIUM),
            onAction = {},
            onContinueClick = {},
            onHistoryClick = {},
            onStatisticsClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentContinuePreview() {
    TecTheme(darkTheme = true) {
        HomeContent(
            state = HomeUiState(
                isLoading = false,
                inProgressGame = HomeInProgressGameUiModel(
                    gameId = "game-1",
                    difficulty = Difficulty.HARD,
                    gridSize = GridSize(rows = 8, columns = 8),
                ),
            ),
            onAction = {},
            onContinueClick = {},
            onHistoryClick = {},
            onStatisticsClick = {},
        )
    }
}
