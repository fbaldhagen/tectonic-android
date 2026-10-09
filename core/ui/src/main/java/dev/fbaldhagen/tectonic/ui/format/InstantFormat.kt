package dev.fbaldhagen.tectonic.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Instant

/** Medium-length date in the current locale and time zone, e.g. "Oct 8, 2026". */
@Composable
fun Instant.toDisplayDate(): String {
    val locale = LocalConfiguration.current.locales[0]
    return remember(this, locale) {
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(locale)
            .format(
                java.time.Instant
                    .ofEpochMilli(toEpochMilliseconds())
                    .atZone(ZoneId.systemDefault())
            )
    }
}
