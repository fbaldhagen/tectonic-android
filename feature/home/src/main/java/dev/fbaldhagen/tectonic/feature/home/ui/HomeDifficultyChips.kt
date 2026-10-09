package dev.fbaldhagen.tectonic.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.ui.text.toUiText

@Composable
internal fun HomeDifficultyChips(
    selected: Difficulty,
    enabled: Boolean,
    onSelect: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Difficulty.entries.forEach { difficulty ->
            FilterChip(
                selected = difficulty == selected,
                onClick = { onSelect(difficulty) },
                enabled = enabled,
                label = { Text(difficulty.toUiText().asString()) },
            )
        }
    }
}
