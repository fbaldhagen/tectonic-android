package dev.fbaldhagen.tectonic.navigation

import androidx.navigation3.runtime.EntryProviderScope

/**
 * Allows a feature module to contribute NavEntry definitions without requiring
 * the application module to know that feature's implementation details.
 */
typealias TectonicEntryProviderInstaller =
        EntryProviderScope<AppNavKey>.(TectonicNavigator) -> Unit