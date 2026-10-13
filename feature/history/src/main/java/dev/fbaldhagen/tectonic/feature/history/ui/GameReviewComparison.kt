package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.TimeComparison
import dev.fbaldhagen.tectonic.ui.format.toClockString
import dev.fbaldhagen.tectonic.ui.text.toUiText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** How the result stacks up against the player's best and average for this difficulty. */
@Composable
internal fun GameReviewComparison(
    comparison: TimeComparison,
    difficulty: Difficulty,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (comparison) {
            TimeComparison.FirstOfDifficulty -> GameReviewDeltaChip(
                icon = Icons.Filled.Star,
                text = stringResource(R.string.review_first_game, difficulty.toUiText().asString()),
                isHighlighted = true,
            )

            is TimeComparison.Compared -> {
                if (comparison.isPersonalBest) {
                    GameReviewDeltaChip(
                        icon = Icons.Filled.EmojiEvents,
                        text = stringResource(R.string.review_personal_best),
                        isHighlighted = true,
                    )
                } else {
                    GameReviewDeltaChip(
                        icon = Icons.Filled.Timer,
                        text = stringResource(
                            R.string.review_slower_than_best,
                            comparison.deltaToBest.toClockString(),
                        ),
                        isHighlighted = false,
                    )
                }
                AverageChip(comparison.deltaToAverage)
            }
        }
    }
}

@Composable
private fun AverageChip(deltaToAverage: Duration) {
    val gap = deltaToAverage.absoluteValue

    when {
        gap < ON_PAR_THRESHOLD -> GameReviewDeltaChip(
            icon = Icons.Filled.Timer,
            text = stringResource(R.string.review_on_par_with_average),
            isHighlighted = false,
        )

        deltaToAverage.isNegative() -> GameReviewDeltaChip(
            icon = Icons.Filled.Speed,
            text = stringResource(R.string.review_faster_than_average, gap.toClockString()),
            isHighlighted = true,
        )

        else -> GameReviewDeltaChip(
            icon = Icons.Filled.Timer,
            text = stringResource(R.string.review_slower_than_average, gap.toClockString()),
            isHighlighted = false,
        )
    }
}

/** A difference under a second is noise, not a result worth announcing. */
private val ON_PAR_THRESHOLD = 1.seconds
