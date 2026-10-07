package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.GridSize

/**
 * Generation parameters for a difficulty tier.
 *
 * @property clueRatio range of the fraction of cells left as givens
 * @property requiresLogicalSolution when true the puzzle must be solvable without guessing
 */
internal data class DifficultyProfile(
    val gridSize: GridSize,
    val clueRatio: ClosedFloatingPointRange<Double>,
    val requiresLogicalSolution: Boolean
) {
    val cellCount: Int get() = gridSize.rows * gridSize.columns

    companion object {
        fun of(difficulty: Difficulty): DifficultyProfile = when (difficulty) {
            Difficulty.EASY -> DifficultyProfile(
                gridSize = GridSize(5, 5),
                clueRatio = 0.55..0.72,
                requiresLogicalSolution = true
            )
            Difficulty.MEDIUM -> DifficultyProfile(
                gridSize = GridSize(6, 6),
                clueRatio = 0.40..0.55,
                requiresLogicalSolution = true
            )
            Difficulty.HARD -> DifficultyProfile(
                gridSize = GridSize(8, 8),
                clueRatio = 0.28..0.40,
                requiresLogicalSolution = false
            )
        }
    }
}
