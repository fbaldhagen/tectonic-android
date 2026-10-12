package dev.fbaldhagen.tectonic.domain.model

enum class Difficulty(val gridSize: GridSize) {
    EASY(GridSize(rows = 5, columns = 5)),
    MEDIUM(GridSize(rows = 6, columns = 6)),
    HARD(GridSize(rows = 8, columns = 8))
}