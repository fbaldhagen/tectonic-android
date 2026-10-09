package dev.fbaldhagen.tectonic.feature.statistics.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.fbaldhagen.tectonic.designsystem.component.TecEmptyState
import dev.fbaldhagen.tectonic.designsystem.component.TecScreenHeader
import dev.fbaldhagen.tectonic.designsystem.theme.TecTheme
import dev.fbaldhagen.tectonic.feature.statistics.R

/** Placeholder until statistics are built, so the Home entry point has a destination. */
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TecScreenHeader(
                title = stringResource(R.string.statistics_title),
                onBackClick = onNavigateBack,
                backContentDescription = stringResource(R.string.statistics_back),
            )
            Box(modifier = Modifier.weight(1f)) {
                TecEmptyState(
                    title = stringResource(R.string.statistics_coming_soon_title),
                    description = stringResource(R.string.statistics_coming_soon_description),
                    icon = Icons.Outlined.BarChart,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsScreenPreview() {
    TecTheme { StatisticsScreen(onNavigateBack = {}) }
}
