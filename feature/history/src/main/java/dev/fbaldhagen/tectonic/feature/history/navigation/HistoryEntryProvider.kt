package dev.fbaldhagen.tectonic.feature.history.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.fbaldhagen.tectonic.feature.history.ui.GameReviewScreen
import dev.fbaldhagen.tectonic.feature.history.ui.HistoryScreen
import dev.fbaldhagen.tectonic.navigation.AppNavKey
import dev.fbaldhagen.tectonic.navigation.TectonicEntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object HistoryEntryProviderModule {

    @Provides
    @IntoSet
    fun provideHistoryEntryProviderInstaller(): TectonicEntryProviderInstaller = { navigator ->
        entry<AppNavKey.History> {
            HistoryScreen(
                onNavigateBack = { navigator.goBack() },
                onGameClick = { gameId -> navigator.navigate(AppNavKey.GameReview(gameId)) },
            )
        }
        entry<AppNavKey.GameReview> { key ->
            GameReviewScreen(
                gameId = key.gameId,
                onNavigateBack = { navigator.goBack() },
                onPlayAgainStarted = {
                    navigator.replaceStack(AppNavKey.Home)
                    navigator.navigate(AppNavKey.Puzzle)
                },
            )
        }
    }
}
