package dev.fbaldhagen.tectonic.feature.home.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.RegionId
import dev.fbaldhagen.tectonic.ui.board.TecBoard
import dev.fbaldhagen.tectonic.ui.board.TecBoardCellUiModel
import dev.fbaldhagen.tectonic.ui.board.TecBoardUiModel

/** A real, rule-abiding 5x5 board shown as artwork: tilted, elevated and purely decorative. */
@Composable
internal fun HomeHero(modifier: Modifier = Modifier) {
    val board = remember { decorativeBoard() }
    val shape = RoundedCornerShape(24.dp)

    TecBoard(
        board = board,
        modifier = modifier
            .size(HERO_SIZE)
            .graphicsLayer {
                rotationZ = HERO_TILT_DEGREES
                shadowElevation = 24.dp.toPx()
                this.shape = shape
                clip = true
            }
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = shape,
            )
            .clearAndSetSemantics { },
    )
}

private val HERO_SIZE = 232.dp
private const val HERO_TILT_DEGREES = -3f

// Letters mark regions, digits are values, and '#' marks a given. A digit without '#' is
// shown as a value the player has entered.
private val HERO_REGIONS = listOf("ABCCC", "ABBCD", "EBFDD", "EFFDG", "EFFGG")
private val HERO_VALUES = listOf("1313.", "2424.", "3.532", "242.1", "13.3.")
private val HERO_GIVENS = listOf("#.##.", "##.#.", "#.###", "##..#", "#..#.")

private fun decorativeBoard(): TecBoardUiModel {
    val cells = buildList {
        for (row in HERO_REGIONS.indices) {
            for (column in HERO_REGIONS[row].indices) {
                add(
                    TecBoardCellUiModel(
                        cellId = CellId(row, column),
                        regionId = RegionId(HERO_REGIONS[row][column] - 'A'),
                        value = HERO_VALUES[row][column].digitToIntOrNull(),
                        isGiven = HERO_GIVENS[row][column] == '#',
                    ),
                )
            }
        }
    }
    return TecBoardUiModel(rows = HERO_REGIONS.size, columns = HERO_REGIONS[0].length, cells = cells)
}
