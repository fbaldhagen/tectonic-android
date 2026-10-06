package dev.fbaldhagen.tectonic.domain.model

data class Puzzle(
    val id: PuzzleId,
    val gridSize: GridSize,
    val cells: List<Cell>,
    val regions: List<Region>,
    val solution: Map<CellId, Int>,
    val difficulty: Difficulty
)