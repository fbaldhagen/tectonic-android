package dev.fbaldhagen.tectonic.domain.service

import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.MoveValidation
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleState

interface MoveValidator {
    fun validate(
        puzzle: Puzzle,
        state: PuzzleState,
        cellId: CellId,
        value: Int
    ): MoveValidation
}