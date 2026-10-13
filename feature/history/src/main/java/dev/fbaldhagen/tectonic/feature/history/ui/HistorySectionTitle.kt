package dev.fbaldhagen.tectonic.feature.history.ui

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import dev.fbaldhagen.tectonic.feature.history.R
import dev.fbaldhagen.tectonic.feature.history.presentation.HistorySectionKey
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
internal fun HistorySectionKey.title(): String = when (this) {
    HistorySectionKey.Today -> stringResource(R.string.history_section_today)
    HistorySectionKey.Yesterday -> stringResource(R.string.history_section_yesterday)
    HistorySectionKey.ThisWeek -> stringResource(R.string.history_section_this_week)
    is HistorySectionKey.Month -> monthTitle(month)
}

/** "October 2026" in the user's language, using the locale's preferred word order. */
@Composable
private fun monthTitle(month: YearMonth): String {
    val locale = LocalConfiguration.current.locales[0]
    return remember(month, locale) {
        val pattern = DateFormat.getBestDateTimePattern(locale, "yMMMM")
        DateTimeFormatter.ofPattern(pattern, locale).format(month)
    }
}
