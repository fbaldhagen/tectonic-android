package dev.fbaldhagen.tectonic.feature.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * The main call to action: Continue when a game is in progress, otherwise the difficulty
 * cards and Play. Content cross-fades and the card resizes when the mode changes.
 */
@Composable
internal fun HomeActionCard(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .widthIn(max = CARD_MAX_WIDTH)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedContent(
                targetState = state.modeOf(),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "homeActionMode",
            ) { mode ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (mode) {
                        ActionMode.Loading -> LoadingContent()

                        ActionMode.Continue -> state.inProgressGame?.let { game ->
                            ContinueContent(
                                game = game,
                                onContinueClick = onContinueClick,
                                onNewGameClick = { onAction(HomeAction.NewGameRequested) },
                            )
                        }

                        ActionMode.NewGame -> NewGameContent(state = state, onAction = onAction)
                    }
                }
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
}

private enum class ActionMode { Loading, Continue, NewGame }

private fun HomeUiState.modeOf(): ActionMode = when {
    isLoading -> ActionMode.Loading
    inProgressGame != null -> ActionMode.Continue
    else -> ActionMode.NewGame
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
    }
}

@Composable
private fun NewGameContent(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    Text(
        text = stringResource(R.string.home_difficulty_label),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
    )
    HomeDifficultyCards(
        selected = state.selectedDifficulty,
        isEnabled = !state.isStartingGame,
        onSelect = { onAction(HomeAction.SelectDifficulty(it)) },
    )
    Spacer(modifier = Modifier.height(20.dp))
    TecPrimaryButton(
        text = stringResource(
            if (state.isStartingGame) R.string.home_generating else R.string.home_play,
        ),
        onClick = { onAction(HomeAction.StartNewGame) },
        isLoading = state.isStartingGame,
        leadingIcon = if (state.isStartingGame) null else Icons.Filled.PlayArrow,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ContinueContent(
    game: HomeInProgressGameUiModel,
    onContinueClick: () -> Unit,
    onNewGameClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.home_in_progress),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(difficultyHue(game.difficulty)),
        )
        Text(
            text = stringResource(
                R.string.home_game_summary,
                game.difficulty.toUiText().asString(),
                game.gridSize.toUiText().asString(),
            ),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    TecPrimaryButton(
        text = stringResource(R.string.home_continue),
        onClick = onContinueClick,
        leadingIcon = Icons.Filled.PlayArrow,
        modifier = Modifier.fillMaxWidth(),
    )
    TextButton(onClick = onNewGameClick) {
        Text(stringResource(R.string.home_new_game))
    }
}

private val CARD_MAX_WIDTH = 420.dp
