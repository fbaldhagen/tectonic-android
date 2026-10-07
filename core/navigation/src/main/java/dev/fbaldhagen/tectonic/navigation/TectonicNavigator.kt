package dev.fbaldhagen.tectonic.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.compose.runtime.saveable.rememberSerializable
import kotlinx.serialization.serializer

/**
 * Owns mutations of the application's Navigation 3 back stack.
 *
 * The back stack itself is created with Compose's savable state mechanism so
 * navigation state survives recomposition, activity recreation, and process
 * death when Android restores saved state.
 */
class TectonicNavigator internal constructor(
    internal val backStack: NavBackStack<AppNavKey>,
) {
    /** Adds a destination to the top of the back stack. */
    fun navigate(destination: AppNavKey) {
        backStack.add(destination)
    }

    /**
     * Removes the current destination when another destination remains below it.
     *
     * @return true when a destination was removed, false when the root remains.
     */
    fun goBack(): Boolean {
        if (backStack.size <= 1) {
            return false
        }

        backStack.removeLastOrNull()
        return true
    }

    /** Replaces the complete back stack with a single destination. */
    fun replaceStack(destination: AppNavKey) {
        backStack.clear()
        backStack.add(destination)
    }

    /** Returns whether system/application back can remove the current destination. */
    fun canGoBack(): Boolean = backStack.size > 1
}

/**
 * Creates a savable application navigator with [startDestination] as its root.
 */
@Composable
fun rememberTectonicNavigator(
    startDestination: AppNavKey,
): TectonicNavigator {
    val backStack = rememberSerializable(
        serializer = serializer<NavBackStack<AppNavKey>>()
    ) {
        NavBackStack(startDestination)
    }

    return remember(backStack) {
        TectonicNavigator(backStack)
    }
}