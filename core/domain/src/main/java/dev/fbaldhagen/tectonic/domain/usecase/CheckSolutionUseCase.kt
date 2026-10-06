package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.SolutionCheckResult
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository
import dev.fbaldhagen.tectonic.domain.service.SolutionChecker

class CheckSolutionUseCase(
    private val gameProgressRepository: GameProgressRepository,
    private val solutionChecker: SolutionChecker
) {
    suspend operator fun invoke(
        gameId: GameId
    ): TecResult<SolutionCheckResult> {
        return when (val result = gameProgressRepository.getGame(gameId)) {
            is TecResult.Success -> {
                TecResult.Success(
                    solutionChecker.check(
                        puzzle = result.data.puzzle,
                        state = result.data.state
                    )
                )
            }

            is TecResult.Error -> result
        }
    }
}