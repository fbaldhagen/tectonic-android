package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.designsystem.component.TecStatTile
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.GameReviewUiModel
import dev.fbaldhagen.tectonic.ui.format.toDisplayDate
import dev.fbaldhagen.tectonic.ui.text.toUiText

@Composable
internal fun GameReviewDetails(
    review: GameReviewUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TecStatTile(
            value = review.date.toDisplayDate(),
            label = stringResource(R.string.review_date),
            modifier = Modifier.weight(1f),
        )
        TecStatTile(
            value = review.gridSize.toUiText().asString(),
            label = stringResource(R.string.review_grid),
            modifier = Modifier.weight(1f),
        )
    }
}
