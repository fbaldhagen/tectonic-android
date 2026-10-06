package dev.fbaldhagen.tectonic.domain.service

import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleState
import dev.fbaldhagen.tectonic.domain.model.SolutionCheckResult

interface SolutionChecker {
    fun check(
        puzzle: Puzzle,
        state: PuzzleState
    ): SolutionCheckResult
}