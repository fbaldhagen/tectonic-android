package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.service.PuzzleGenerator
import dev.fbaldhagen.tectonic.domain.service.PuzzleSeedProvider

class GeneratePuzzleUseCase(
    private val puzzleGenerator: PuzzleGenerator,
    private val seedProvider: PuzzleSeedProvider
) {
    operator fun invoke(
        difficulty: Difficulty
    ): TecResult<Puzzle> = puzzleGenerator.generate(difficulty, seedProvider.nextSeed())
}