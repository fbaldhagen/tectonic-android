package dev.fbaldhagen.tectonic.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.fbaldhagen.tectonic.data.local.TectonicDatabase
import dev.fbaldhagen.tectonic.data.local.dao.GameDao
import dev.fbaldhagen.tectonic.data.local.dao.PuzzleDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TectonicDatabase {
        return Room.databaseBuilder(
            context,
            TectonicDatabase::class.java,
            "tectonic.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun providePuzzleDao(database: TectonicDatabase): PuzzleDao =
        database.puzzleDao()

    @Provides
    fun provideGameDao(database: TectonicDatabase): GameDao =
        database.gameDao()
}