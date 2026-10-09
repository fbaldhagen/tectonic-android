package dev.fbaldhagen.tectonic.feature.statistics.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.fbaldhagen.tectonic.feature.statistics.ui.StatisticsScreen
import dev.fbaldhagen.tectonic.navigation.AppNavKey
import dev.fbaldhagen.tectonic.navigation.TectonicEntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object StatisticsEntryProviderModule {

    @Provides
    @IntoSet
    fun provideStatisticsEntryProviderInstaller(): TectonicEntryProviderInstaller = { navigator ->
        entry<AppNavKey.Statistics> {
            StatisticsScreen(onNavigateBack = { navigator.goBack() })
        }
    }
}
