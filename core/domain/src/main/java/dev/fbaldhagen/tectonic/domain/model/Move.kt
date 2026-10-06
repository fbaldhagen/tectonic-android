package dev.fbaldhagen.tectonic.domain.model

data class Move(
    val cellId: CellId,
    val previousValue: Int?,
    val newValue: Int?
)