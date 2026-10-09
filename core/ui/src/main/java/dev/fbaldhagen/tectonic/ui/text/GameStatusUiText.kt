package dev.fbaldhagen.tectonic.ui.text

import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.ui.R

fun GameStatus.toUiText(): UiText = UiText.Resource(
    when (this) {
        GameStatus.IN_PROGRESS -> R.string.status_in_progress
        GameStatus.COMPLETED -> R.string.status_completed
        GameStatus.ABANDONED -> R.string.status_abandoned
    }
)
