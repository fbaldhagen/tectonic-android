package dev.fbaldhagen.tectonic.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Type-safe application destinations.
 *
 * Feature modules depend on this API when they need to navigate to another
 * application destination. Destination content remains owned by the feature
 * module that implements it.
 */
@Serializable
sealed interface AppNavKey : NavKey {
    @Serializable
    data object AuthWelcome : AppNavKey

    @Serializable
    data object AuthLogin : AppNavKey

    @Serializable
    data object AuthSignUp : AppNavKey

    @Serializable
    data object Home : AppNavKey

    @Serializable
    data object Puzzle : AppNavKey

    @Serializable
    data object History : AppNavKey

    @Serializable
    data object Statistics : AppNavKey

    @Serializable
    data object Settings : AppNavKey
}