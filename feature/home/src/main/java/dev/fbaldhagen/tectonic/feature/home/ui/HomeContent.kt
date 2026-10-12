package dev.fbaldhagen.tectonic.feature.home.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.domain.model.Difficulty
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
    val colors = MaterialTheme.colorScheme

    // Play the entrance once per session; coming back from the puzzle shows Home as it was.
    var hasEntered by rememberSaveable { mutableStateOf(false) }
    val entrance = remember { MutableTransitionState(hasEntered) }
    LaunchedEffect(Unit) {
        entrance.targetState = true
        hasEntered = true
    }

    Scaffold(
        modifier = modifier.background(
            Brush.verticalGradient(
                0f to colors.primaryContainer.copy(alpha = 0.7f),
                0.55f to colors.background,
                1f to colors.background,
            ),
        ),
        containerColor = Color.Transparent,
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
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Entrance(entrance, delayMillis = 0) { HomeHero() }
                Spacer(modifier = Modifier.height(28.dp))
                Entrance(entrance, delayMillis = 80) { HomeBranding() }
                Spacer(modifier = Modifier.height(28.dp))
                Entrance(entrance, delayMillis = 160) {
                    HomeActionCard(
                        state = state,
                        onAction = onAction,
                        onContinueClick = onContinueClick,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Entrance(entrance, delayMillis = 240) {
                    HomeHistoryTile(onClick = onHistoryClick)
                }
                Spacer(modifier = Modifier.height(24.dp))
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

@Composable
private fun ColumnScope.Entrance(
    visibleState: MutableTransitionState<Boolean>,
    delayMillis: Int,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = enterTransition(delayMillis),
    ) {
        content()
    }
}

private fun enterTransition(delayMillis: Int): EnterTransition =
    fadeIn(animationSpec = tween(durationMillis = 420, delayMillis = delayMillis)) +
        slideInVertically(
            animationSpec = tween(durationMillis = 420, delayMillis = delayMillis),
            initialOffsetY = { it / 8 },
        )

@Preview(showBackground = true, heightDp = 800)
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

@Preview(showBackground = true, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentContinuePreview() {
    TecTheme(darkTheme = true) {
        HomeContent(
            state = HomeUiState(
                isLoading = false,
                inProgressGame = HomeInProgressGameUiModel(
                    gameId = "game-1",
                    difficulty = Difficulty.HARD,
                    gridSize = Difficulty.HARD.gridSize,
                ),
            ),
            onAction = {},
            onContinueClick = {},
            onHistoryClick = {},
            onStatisticsClick = {},
        )
    }
}
