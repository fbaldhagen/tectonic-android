package dev.fbaldhagen.tectonic.domain.model

data class PuzzleState(
    val values: Map<CellId, Int?>,
    val moves: List<Move>
)