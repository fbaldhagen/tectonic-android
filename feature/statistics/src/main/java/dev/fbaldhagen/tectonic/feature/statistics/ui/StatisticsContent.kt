package dev.fbaldhagen.tectonic.feature.statistics.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.component.TecEmptyState
import dev.fbaldhagen.tectonic.designsystem.component.TecErrorState
import dev.fbaldhagen.tectonic.designsystem.component.TecLoadingState
import dev.fbaldhagen.tectonic.designsystem.component.TecScreenHeader
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.DifficultyTimes
import dev.fbaldhagen.tectonic.domain.model.GameStatistics
import dev.fbaldhagen.tectonic.feature.statistics.R
import dev.fbaldhagen.tectonic.feature.statistics.presentation.StatisticsAction
import dev.fbaldhagen.tectonic.feature.statistics.presentation.StatisticsUiState
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun StatisticsContent(
    state: StatisticsUiState,
    onAction: (StatisticsAction) -> Unit,
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
            TecScreenHeader(
                title = stringResource(R.string.statistics_title),
                onBackClick = onBackClick,
                backContentDescription = stringResource(R.string.statistics_back),
            )

            val statistics = state.statistics
            Box(modifier = Modifier.weight(1f)) {
                when {
                    statistics != null -> StatisticsBody(
                        state = state,
                        statistics = statistics,
                        onAction = onAction,
                    )

                    state.isLoading -> TecLoadingState()

                    state.error != null -> TecErrorState(
                        message = state.error.asString(),
                        onRetryClick = { onAction(StatisticsAction.Retry) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsBody(
    state: StatisticsUiState,
    statistics: GameStatistics,
    onAction: (StatisticsAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        StatisticsFilterRow(
            selected = state.selectedDifficulty,
            onSelect = { onAction(StatisticsAction.SelectDifficulty(it)) },
        )

        if (statistics.played == 0) {
            Box(modifier = Modifier.weight(1f)) {
                TecEmptyState(
                    title = stringResource(R.string.statistics_empty_title),
                    description = stringResource(R.string.statistics_empty_description),
                    icon = Icons.Outlined.BarChart,
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StatisticsOverviewCard(statistics)
                StatisticsTimesCard(statistics.times)
                StatisticsStreakCard(
                    currentDays = statistics.currentStreakDays,
                    longestDays = statistics.longestStreakDays,
                )
                StatisticsTrendCard(
                    difficulty = state.selectedDifficulty,
                    recentSolveTimes = statistics.recentSolveTimes,
                )
            }
        }
    }
}

private fun previewStatistics() = GameStatistics(
    played = 24,
    completed = 19,
    abandoned = 5,
    completionRate = 19.0 / 24.0,
    totalTime = 212.minutes,
    times = listOf(
        DifficultyTimes(Difficulty.EASY, 9, 2.minutes + 4.seconds, 3.minutes + 10.seconds),
        DifficultyTimes(Difficulty.MEDIUM, 7, 5.minutes + 40.seconds, 8.minutes + 2.seconds),
        DifficultyTimes(Difficulty.HARD, 3, 14.minutes + 5.seconds, 18.minutes + 30.seconds),
    ),
    currentStreakDays = 3,
    longestStreakDays = 6,
    recentSolveTimes = listOf(260, 240, 300, 210, 190, 230, 180, 205, 170, 160).map { it.seconds },
)

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun StatisticsContentPreview() {
    TecTheme {
        StatisticsContent(
            state = StatisticsUiState(
                isLoading = false,
                selectedDifficulty = Difficulty.EASY,
                statistics = previewStatistics(),
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatisticsContentAllDarkPreview() {
    TecTheme(darkTheme = true) {
        StatisticsContent(
            state = StatisticsUiState(isLoading = false, statistics = previewStatistics()),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsContentEmptyPreview() {
    TecTheme {
        StatisticsContent(
            state = StatisticsUiState(
                isLoading = false,
                statistics = previewStatistics().copy(played = 0, completed = 0, abandoned = 0),
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}
