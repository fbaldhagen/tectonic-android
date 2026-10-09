package dev.fbaldhagen.tectonic.feature.home.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.fbaldhagen.tectonic.feature.home.ui.HomeScreen
import dev.fbaldhagen.tectonic.navigation.AppNavKey
import dev.fbaldhagen.tectonic.navigation.TectonicEntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object HomeEntryProviderModule {

    @Provides
    @IntoSet
    fun provideHomeEntryProviderInstaller(): TectonicEntryProviderInstaller = { navigator ->
        entry<AppNavKey.Home> {
            HomeScreen(
                onContinueClick = { navigator.navigate(AppNavKey.Puzzle) },
                onGameStarted = { navigator.navigate(AppNavKey.Puzzle) },
                onHistoryClick = { navigator.navigate(AppNavKey.History) },
                onStatisticsClick = { navigator.navigate(AppNavKey.Statistics) },
            )
        }
    }
}
