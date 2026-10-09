package dev.fbaldhagen.tectonic.ui.text

import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.ui.R

fun GridSize.toUiText(): UiText = UiText.Resource(R.string.grid_size_label, rows, columns)
