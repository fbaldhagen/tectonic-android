package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.component.TecErrorState
import dev.fbaldhagen.tectonic.designsystem.component.TecLoadingState
import dev.fbaldhagen.tectonic.designsystem.component.TecScreenHeader
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewAction
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewUiState
import dev.fbaldhagen.tectonic.ui.text.toUiText

@Composable
fun GameReviewContent(
    state: GameReviewUiState,
    onAction: (GameReviewAction) -> Unit,
    onBackClick: () -> Unit,
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
            val review = state.review
            TecScreenHeader(
                title = review?.let {
                    stringResource(
                        R.string.history_row_title,
                        it.difficulty.toUiText().asString(),
                        it.gridSize.toUiText().asString(),
                    )
                } ?: stringResource(R.string.history_title),
                onBackClick = onBackClick,
                backContentDescription = stringResource(R.string.history_back),
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    state.isLoading -> TecLoadingState()

                    state.error != null -> TecErrorState(
                        message = state.error.asString(),
                        onRetryClick = { onAction(GameReviewAction.Retry) },
                    )

                    review != null -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        GameReviewHero(review, Modifier.widthIn(max = MAX_CONTENT_WIDTH))
                        GameReviewBoardCard(review.board, Modifier.widthIn(max = MAX_CONTENT_WIDTH))
                        GameReviewDetails(review, Modifier.widthIn(max = MAX_CONTENT_WIDTH))
                    }
                }
            }

            if (review != null) {
                GameReviewPlayAgainBar(
                    isStarting = state.isStartingGame,
                    error = state.playAgainError,
                    onPlayAgainClick = { onAction(GameReviewAction.PlayAgain) },
                )
            }
        }
    }

    if (state.isAbandonDialogVisible) {
        GameReviewAbandonDialog(
            onConfirm = { onAction(GameReviewAction.AbandonConfirmed) },
            onDismiss = { onAction(GameReviewAction.AbandonDismissed) },
        )
    }
}

private val MAX_CONTENT_WIDTH = 440.dp