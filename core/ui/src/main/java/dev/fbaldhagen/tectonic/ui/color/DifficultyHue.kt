package dev.fbaldhagen.tectonic.ui.color

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.domain.model.Difficulty

/** Green, amber and rose read as easy, medium and hard; they come from the region palette. */
@Composable
fun difficultyHue(difficulty: Difficulty): Color {
    val index = when (difficulty) {
        Difficulty.EASY -> 1
        Difficulty.MEDIUM -> 2
        Difficulty.HARD -> 3
    }
    return TecTheme.extendedColors.regionHues.getOrElse(index) {
        MaterialTheme.colorScheme.surfaceVariant
    }
}
