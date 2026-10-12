package dev.fbaldhagen.tectonic.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.fbaldhagen.tectonic.domain.repository.GameHistoryRepository
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository
import dev.fbaldhagen.tectonic.domain.service.DomainClock
import dev.fbaldhagen.tectonic.domain.service.GameIdProvider
import dev.fbaldhagen.tectonic.domain.service.GameStatisticsCalculator
import dev.fbaldhagen.tectonic.domain.service.MoveValidator
import dev.fbaldhagen.tectonic.domain.service.PuzzleGenerator
import dev.fbaldhagen.tectonic.domain.service.PuzzleSeedProvider
import dev.fbaldhagen.tectonic.domain.service.PuzzleSolver
import dev.fbaldhagen.tectonic.domain.service.PuzzleValidator
import dev.fbaldhagen.tectonic.domain.service.SolutionChecker
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultDomainClock
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultGameIdProvider
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultGameStatisticsCalculator
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultMoveValidator
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultPuzzleGenerator
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultPuzzleSeedProvider
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultPuzzleSolver
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultPuzzleValidator
import dev.fbaldhagen.tectonic.domain.service.implementation.DefaultSolutionChecker
import dev.fbaldhagen.tectonic.domain.usecase.AbandonGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.CheckSolutionUseCase
import dev.fbaldhagen.tectonic.domain.usecase.ClearCellUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GeneratePuzzleUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetCurrentGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetGameHistoryUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetInProgressGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.GetStatisticsUseCase
import dev.fbaldhagen.tectonic.domain.usecase.MakeMoveUseCase
import dev.fbaldhagen.tectonic.domain.usecase.StartNewGameUseCase
import dev.fbaldhagen.tectonic.domain.usecase.UndoMoveUseCase
import dev.fbaldhagen.tectonic.domain.usecase.ValidateMoveUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDomainModule {

    // Domain services (concrete -> interface)
    @Provides
    @Singleton
    fun provideMoveValidator(): MoveValidator = DefaultMoveValidator()

    @Provides
    @Singleton
    fun provideSolutionChecker(): SolutionChecker = DefaultSolutionChecker()

    @Provides
    @Singleton
    fun providePuzzleValidator(): PuzzleValidator = DefaultPuzzleValidator()

    @Provides
    @Singleton
    fun providePuzzleSolver(): PuzzleSolver = DefaultPuzzleSolver()

    @Provides
    @Singleton
    fun providePuzzleGenerator(
        solver: PuzzleSolver,
        validator: PuzzleValidator
    ): PuzzleGenerator = DefaultPuzzleGenerator(solver, validator)

    // Small infrastructure providers
    @Provides
    @Singleton
    fun provideDomainClock(): DomainClock = DefaultDomainClock()

    @Provides
    @Singleton
    fun provideGameIdProvider(): GameIdProvider = DefaultGameIdProvider()

    @Provides
    @Singleton
    fun providePuzzleSeedProvider(): PuzzleSeedProvider = DefaultPuzzleSeedProvider()

    // Use cases
    @Provides
    fun provideGeneratePuzzleUseCase(
        puzzleGenerator: PuzzleGenerator,
        seedProvider: PuzzleSeedProvider
    ): GeneratePuzzleUseCase = GeneratePuzzleUseCase(puzzleGenerator, seedProvider)

    @Provides
    fun provideCheckSolutionUseCase(
        gameProgressRepository: GameProgressRepository,
        solutionChecker: SolutionChecker
    ): CheckSolutionUseCase = CheckSolutionUseCase(gameProgressRepository, solutionChecker)

    @Provides
    fun provideClearCellUseCase(
        gameProgressRepository: GameProgressRepository
    ): ClearCellUseCase = ClearCellUseCase(gameProgressRepository)

    @Provides
    fun provideGetCurrentGameUseCase(
        gameProgressRepository: GameProgressRepository
    ): GetCurrentGameUseCase = GetCurrentGameUseCase(gameProgressRepository)

    @Provides
    fun provideGetGameUseCase(
        gameProgressRepository: GameProgressRepository
    ): GetGameUseCase = GetGameUseCase(gameProgressRepository)

    @Provides
    fun provideMakeMoveUseCase(
        gameProgressRepository: GameProgressRepository,
        moveValidator: MoveValidator,
        solutionChecker: SolutionChecker,
        clock: DomainClock
    ): MakeMoveUseCase =
        MakeMoveUseCase(gameProgressRepository, moveValidator, solutionChecker, clock)

    @Provides
    fun provideAbandonGameUseCase(
        gameProgressRepository: GameProgressRepository
    ): AbandonGameUseCase = AbandonGameUseCase(gameProgressRepository)

    @Provides
    fun provideGetInProgressGameUseCase(
        gameHistoryRepository: GameHistoryRepository
    ): GetInProgressGameUseCase = GetInProgressGameUseCase(gameHistoryRepository)

    @Provides
    fun provideGameStatisticsCalculator(): GameStatisticsCalculator =
        DefaultGameStatisticsCalculator()

    @Provides
    fun provideGetStatisticsUseCase(
        gameHistoryRepository: GameHistoryRepository,
        calculator: GameStatisticsCalculator,
        clock: DomainClock
    ): GetStatisticsUseCase = GetStatisticsUseCase(gameHistoryRepository, calculator, clock)

    @Provides
    fun provideGetGameHistoryUseCase(
        gameHistoryRepository: GameHistoryRepository
    ): GetGameHistoryUseCase = GetGameHistoryUseCase(gameHistoryRepository)

    @Provides
    fun provideStartNewGameUseCase(
        generatePuzzleUseCase: GeneratePuzzleUseCase,
        gameProgressRepository: GameProgressRepository,
        gameIdProvider: GameIdProvider,
        clock: DomainClock
    ): StartNewGameUseCase =
        StartNewGameUseCase(generatePuzzleUseCase, gameProgressRepository, gameIdProvider, clock)

    @Provides
    fun provideUndoMoveUseCase(
        gameProgressRepository: GameProgressRepository
    ): UndoMoveUseCase = UndoMoveUseCase(gameProgressRepository)

    @Provides
    fun provideValidateMoveUseCase(
        gameProgressRepository: GameProgressRepository,
        moveValidator: MoveValidator
    ): ValidateMoveUseCase = ValidateMoveUseCase(gameProgressRepository, moveValidator)
}