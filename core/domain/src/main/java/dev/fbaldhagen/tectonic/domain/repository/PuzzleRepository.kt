package dev.fbaldhagen.tectonic.domain.repository

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleId

interface PuzzleRepository {

    suspend fun getPuzzle(
        puzzleId: PuzzleId
    ): TecResult<Puzzle>

    suspend fun savePuzzle(
        puzzle: Puzzle
    ): TecResult<Unit>
}