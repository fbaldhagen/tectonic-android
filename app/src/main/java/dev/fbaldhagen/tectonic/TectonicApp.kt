package dev.fbaldhagen.tectonic

import androidx.compose.runtime.Composable
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.navigation.AppNavKey
import dev.fbaldhagen.tectonic.navigation.TectonicEntryProviderInstaller
import dev.fbaldhagen.tectonic.navigation.TectonicNavHost
import dev.fbaldhagen.tectonic.navigation.rememberTectonicNavigator

@Composable
fun TectonicApp(
    startDestination: AppNavKey,
    entryProviderInstallers: Collection<TectonicEntryProviderInstaller>,
) {
    TecTheme {
        val navigator = rememberTectonicNavigator(startDestination)

        TectonicNavHost(
            navigator = navigator,
            entryProviderInstallers = entryProviderInstallers,
        )
    }
}