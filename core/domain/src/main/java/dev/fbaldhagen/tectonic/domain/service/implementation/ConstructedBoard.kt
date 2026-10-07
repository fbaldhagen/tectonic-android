package dev.fbaldhagen.tectonic.domain.service.implementation

/** A complete, rule-abiding board: its regions (as flat cell indexes) and the full solution. */
internal class ConstructedBoard(
    val regions: List<IntArray>,
    val solution: IntArray
)
