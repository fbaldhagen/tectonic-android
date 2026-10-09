package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewUiModel
import dev.fbaldhagen.tectonic.ui.format.toClockString
import dev.fbaldhagen.tectonic.ui.format.toDisplayDate
import dev.fbaldhagen.tectonic.ui.text.toUiText

@Composable
internal fun GameReviewDetails(
    review: GameReviewUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DetailRow(
            label = stringResource(R.string.review_status),
            value = review.status.toUiText().asString(),
        )
        review.duration?.let { duration ->
            DetailRow(
                label = stringResource(R.string.review_time),
                value = duration.toClockString(),
            )
        }
        DetailRow(
            label = stringResource(R.string.review_date),
            value = review.date.toDisplayDate(),
        )
        DetailRow(
            label = stringResource(R.string.review_grid),
            value = review.gridSize.toUiText().asString(),
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
