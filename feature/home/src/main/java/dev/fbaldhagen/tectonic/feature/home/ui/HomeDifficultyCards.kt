package dev.fbaldhagen.tectonic.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.Difficulty

@Composable
internal fun HomeDifficultyCards(
    selected: Difficulty,
    isEnabled: Boolean,
    onSelect: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Difficulty.entries.forEach { difficulty ->
            HomeDifficultyCard(
                difficulty = difficulty,
                isSelected = difficulty == selected,
                isEnabled = isEnabled,
                onClick = { onSelect(difficulty) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    }
}
