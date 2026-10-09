package dev.fbaldhagen.tectonic.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.component.TecPrimaryButton
import dev.fbaldhagen.tectonic.feature.home.R
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeAction
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeInProgressGameUiModel
import dev.fbaldhagen.tectonic.feature.home.presentation.HomeUiState
import dev.fbaldhagen.tectonic.ui.text.toUiText

/**
 * The main call to action. Shows Continue when a game is in progress, otherwise
 * Play with the difficulty chips used to pick the new game.
 */
@Composable
internal fun HomePrimaryActions(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(max = 320.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val inProgressGame = state.inProgressGame
        when {
            state.isLoading -> Box(
                modifier = Modifier.height(52.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
            }

            inProgressGame != null -> ContinueActions(
                game = inProgressGame,
                onContinueClick = onContinueClick,
                onNewGameClick = { onAction(HomeAction.NewGameRequested) },
            )

            else -> NewGameActions(state = state, onAction = onAction)
        }

        state.error?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ContinueActions(
    game: HomeInProgressGameUiModel,
    onContinueClick: () -> Unit,
    onNewGameClick: () -> Unit,
) {
    TecPrimaryButton(
        text = stringResource(R.string.home_continue),
        onClick = onContinueClick,
        leadingIcon = Icons.Filled.PlayArrow,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(
            R.string.home_game_summary,
            game.difficulty.toUiText().asString(),
            game.gridSize.toUiText().asString(),
        ),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    TextButton(onClick = onNewGameClick) {
        Text(stringResource(R.string.home_new_game))
    }
}

@Composable
private fun NewGameActions(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    TecPrimaryButton(
        text = stringResource(
            if (state.isStartingGame) R.string.home_generating else R.string.home_play,
        ),
        onClick = { onAction(HomeAction.StartNewGame) },
        isLoading = state.isStartingGame,
        leadingIcon = if (state.isStartingGame) null else Icons.Filled.PlayArrow,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(16.dp))
    HomeDifficultyChips(
        selected = state.selectedDifficulty,
        enabled = !state.isStartingGame,
        onSelect = { onAction(HomeAction.SelectDifficulty(it)) },
    )
}
