package dev.fbaldhagen.tectonic.feature.history.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.ui.board.TecBoard
import dev.fbaldhagen.tectonic.ui.board.TecBoardUiModel

/** The final board, framed like a printed puzzle. Read-only: no click handler is passed. */
@Composable
internal fun GameReviewBoardCard(
    board: TecBoardUiModel,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
    ) {
        TecBoard(
            board = board,
            modifier = Modifier.padding(14.dp),
        )
    }
}
