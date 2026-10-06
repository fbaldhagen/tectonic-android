package dev.fbaldhagen.tectonic.domain.service

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.Puzzle

interface PuzzleGenerator {
    fun generate(
        difficulty: Difficulty,
        seed: Long
    ): TecResult<Puzzle>
}