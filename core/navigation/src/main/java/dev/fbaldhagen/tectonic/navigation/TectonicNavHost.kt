package dev.fbaldhagen.tectonic.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

/**
 * Displays the application's Navigation 3 back stack and installs all feature
 * entry providers contributed by the application composition root.
 */
@Composable
fun TectonicNavHost(
    navigator: TectonicNavigator,
    entryProviderInstallers: Collection<TectonicEntryProviderInstaller>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entryProviderInstallers.forEach { installer ->
                installer(navigator)
            }
        },
    )
}