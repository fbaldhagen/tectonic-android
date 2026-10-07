package dev.fbaldhagen.tectonic.ui.text

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A user-facing string that doesn't require a `Context` to construct — safe to
 * hold in ViewModel state and StateFlows. Resolve it to an actual `String` only
 * at the point of composition (or with an explicit `Context` off the UI thread).
 *
 * Two constructors cover the cases that come up in practice:
 * - [Dynamic] for strings already resolved elsewhere (e.g. from an exception message)
 * - [Resource] for anything that should be localized via `strings.xml`, with
 *   optional format args
 */
sealed interface UiText {
    data class Dynamic(val value: String) : UiText

    class Resource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText

    @Composable
    fun asString(): String = when (this) {
        is Dynamic -> value
        is Resource -> stringResource(resId, *args)
    }

    fun asString(context: Context): String = when (this) {
        is Dynamic -> value
        is Resource -> context.getString(resId, *args)
    }
}