package dev.fbaldhagen.tectonic.ui.text

import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.ui.R

fun Difficulty.toUiText(): UiText = UiText.Resource(
    when (this) {
        Difficulty.EASY -> R.string.difficulty_easy
        Difficulty.MEDIUM -> R.string.difficulty_medium
        Difficulty.HARD -> R.string.difficulty_hard
    }
)
