package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.fbaldhagen.tectonic.designsystem.component.TecScreenHeader
import dev.fbaldhagen.tectonic.domain.model.Difficulty
import dev.fbaldhagen.tectonic.feature.puzzle.R
import dev.fbaldhagen.tectonic.ui.text.toUiText

/**
 * The new-game menu is temporary: Home owns starting games, so this can be deleted
 * together with PuzzleAction.StartNewGame once nothing else needs it.
 */
@Composable
internal fun PuzzleHeader(
    difficulty: Difficulty?,
    onBackClick: () -> Unit,
    onNewGameClick: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    TecScreenHeader(
        title = difficulty?.toUiText()?.asString().orEmpty(),
        onBackClick = onBackClick,
        backContentDescription = stringResource(R.string.puzzle_back),
        modifier = modifier,
    ) {
        IconButton(onClick = { isMenuOpen = true }) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = stringResource(R.string.puzzle_new_game),
            )
        }
        DropdownMenu(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false },
        ) {
            Difficulty.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(entry.toUiText().asString()) },
                    onClick = {
                        isMenuOpen = false
                        onNewGameClick(entry)
                    },
                )
            }
        }
    }
}
