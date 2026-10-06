package dev.fbaldhagen.tectonic.domain.service

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Puzzle

interface PuzzleValidator {
    fun validate(
        puzzle: Puzzle
    ): TecResult<Unit>
}