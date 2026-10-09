package dev.fbaldhagen.tectonic.ui.format

import java.util.Locale
import kotlin.time.Duration

/** Formats as m:ss, or h:mm:ss once past an hour. */
fun Duration.toClockString(): String = toComponents { hours, minutes, seconds, _ ->
    if (hours > 0) {
        String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.ROOT, "%d:%02d", minutes, seconds)
    }
}
