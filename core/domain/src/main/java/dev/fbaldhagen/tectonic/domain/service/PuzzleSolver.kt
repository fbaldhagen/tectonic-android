package dev.fbaldhagen.tectonic.domain.service

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Puzzle

interface PuzzleSolver {
    fun solve(
        puzzle: Puzzle
    ): TecResult<List<Map<CellId, Int>>>
}