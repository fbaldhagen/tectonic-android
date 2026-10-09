package dev.fbaldhagen.tectonic.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.feature.home.R

/** Settings is only shown when [onSettingsClick] is provided, so it can ship once it has content. */
@Composable
internal fun HomeHeader(
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        IconButton(onClick = onStatisticsClick) {
            Icon(
                imageVector = Icons.Outlined.BarChart,
                contentDescription = stringResource(R.string.home_statistics),
            )
        }
        onSettingsClick?.let { onClick ->
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.home_settings),
                )
            }
        }
    }
}
