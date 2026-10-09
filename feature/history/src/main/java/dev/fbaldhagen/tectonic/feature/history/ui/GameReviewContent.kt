package dev.fbaldhagen.tectonic.feature.history.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.component.TecErrorState
import dev.fbaldhagen.tectonic.designsystem.component.TecLoadingState
import dev.fbaldhagen.tectonic.designsystem.component.TecScreenHeader
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.domain.model.RegionId
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewAction
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewUiModel
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewUiState
import dev.fbaldhagen.tectonic.ui.board.TecBoard
import dev.fbaldhagen.tectonic.ui.board.TecBoardCellUiModel
import dev.fbaldhagen.tectonic.ui.board.TecBoardUiModel
import dev.fbaldhagen.tectonic.ui.text.toUiText
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

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
                title = review?.difficulty?.toUiText()?.asString()
                    ?: stringResource(R.string.history_title),
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
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        TecBoard(board = review.board)
                        Spacer(modifier = Modifier.height(24.dp))
                        GameReviewDetails(review = review)
                    }
                }
            }
        }
    }
}

private fun previewReview(): GameReviewUiModel {
    val cells = listOf(
        TecBoardCellUiModel(CellId(0, 0), RegionId(0), 1, isGiven = true),
        TecBoardCellUiModel(CellId(0, 1), RegionId(0), 2, isGiven = false),
        TecBoardCellUiModel(CellId(1, 0), RegionId(1), 2, isGiven = false),
        TecBoardCellUiModel(CellId(1, 1), RegionId(1), 1, isGiven = true),
    )
    return GameReviewUiModel(
        board = TecBoardUiModel(rows = 2, columns = 2, cells = cells),
        difficulty = Difficulty.EASY,
        gridSize = GridSize(2, 2),
        status = GameStatus.COMPLETED,
        duration = 4.minutes,
        date = Instant.fromEpochSeconds(1_790_000_000),
    )
}

@Preview(showBackground = true)
@Composable
private fun GameReviewContentPreview() {
    TecTheme {
        GameReviewContent(
            state = GameReviewUiState(isLoading = false, review = previewReview()),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GameReviewContentDarkPreview() {
    TecTheme(darkTheme = true) {
        GameReviewContent(
            state = GameReviewUiState(isLoading = false, review = previewReview()),
            onAction = {},
            onBackClick = {},
        )
    }
}
