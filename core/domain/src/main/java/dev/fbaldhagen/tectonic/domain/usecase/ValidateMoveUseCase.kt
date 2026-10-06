package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.model.MoveValidation
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository
import dev.fbaldhagen.tectonic.domain.service.MoveValidator

class ValidateMoveUseCase(
    private val gameProgressRepository: GameProgressRepository,
    private val moveValidator: MoveValidator
) {
    suspend operator fun invoke(
        gameId: GameId,
        cellId: CellId,
        value: Int
    ): TecResult<MoveValidation> {
        return when (val result = gameProgressRepository.getGame(gameId)) {
            is TecResult.Success -> {
                TecResult.Success(
                    moveValidator.validate(
                        puzzle = result.data.puzzle,
                        state = result.data.state,
                        cellId = cellId,
                        value = value
                    )
                )
            }

            is TecResult.Error -> result
        }
    }
}