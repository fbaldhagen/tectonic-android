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

/** Short form for long spans, e.g. "3h 12m", "12m 30s" or "45s". */
fun Duration.toCompactString(): String = toComponents { hours, minutes, seconds, _ ->
    when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}