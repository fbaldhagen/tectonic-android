package dev.fbaldhagen.tectonic.domain.usecase

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameStatus
import dev.fbaldhagen.tectonic.domain.model.PuzzleState
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository
import dev.fbaldhagen.tectonic.domain.service.DomainClock
import dev.fbaldhagen.tectonic.domain.service.GameIdProvider

class StartNewGameUseCase(
    private val generatePuzzleUseCase: GeneratePuzzleUseCase,
    private val gameProgressRepository: GameProgressRepository,
    private val gameIdProvider: GameIdProvider,
    private val clock: DomainClock
) {
    suspend operator fun invoke(
        difficulty: Difficulty
    ): TecResult<Game> {
        return when (val puzzleResult = generatePuzzleUseCase(difficulty)) {
            is TecResult.Success -> {
                println("Puzzle generated successfully: ${puzzleResult.data.id}")
                val puzzle = puzzleResult.data

                val initialState = PuzzleState(
                    values = puzzle.cells.associate { cell ->
                        cell.id to cell.givenValue
                    },
                    moves = emptyList()
                )

                val game = Game(
                    id = gameIdProvider.nextId(),
                    puzzle = puzzle,
                    state = initialState,
                    status = GameStatus.IN_PROGRESS,
                    startedAt = clock.now(),
                    result = null
                )

                when (val saveResult = gameProgressRepository.saveGame(game)) {
                    is TecResult.Success -> {
                        println("Game saved successfully: ${game.id}")
                        TecResult.Success(game)
                    }

                    is TecResult.Error -> {
                        println("SaveGame failed: ${saveResult.error}")
                        saveResult
                    }
                }
            }

            is TecResult.Error -> {
                println("GeneratePuzzle failed: ${puzzleResult.error}")
                puzzleResult
            }
        }
    }
}