package dev.fbaldhagen.tectonic.feature.puzzle.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.fbaldhagen.tectonic.feature.puzzle.ui.PuzzleScreen
import dev.fbaldhagen.tectonic.navigation.AppNavKey
import dev.fbaldhagen.tectonic.navigation.TectonicEntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object PuzzleEntryProviderModule {

    @Provides
    @IntoSet
    fun providePuzzleEntryProviderInstaller(): TectonicEntryProviderInstaller = { navigator ->
        entry<AppNavKey.Puzzle> {
            PuzzleScreen(
                onNavigateHome = {
                    navigator.replaceStack(AppNavKey.Home)
                },
            )
        }
    }
}