package dev.fbaldhagen.tectonic.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.fbaldhagen.tectonic.data.repository.GameProgressRepositoryImpl
import dev.fbaldhagen.tectonic.data.repository.PuzzleRepositoryImpl
import dev.fbaldhagen.tectonic.domain.repository.GameProgressRepository
import dev.fbaldhagen.tectonic.domain.repository.PuzzleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPuzzleRepository(
        impl: PuzzleRepositoryImpl
    ): PuzzleRepository

    @Binds
    @Singleton
    abstract fun bindGameProgressRepository(
        impl: GameProgressRepositoryImpl
    ): GameProgressRepository
}