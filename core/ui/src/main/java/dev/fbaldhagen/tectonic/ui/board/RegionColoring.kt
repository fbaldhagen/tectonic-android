package dev.fbaldhagen.tectonic.ui.board

import dev.fbaldhagen.tectonic.domain.model.RegionId

/**
 * Picks a palette index for every region so that regions sharing an edge never share a color.
 *
 * Greedy coloring, busiest regions first. Each region starts from its own id's slot and takes
 * the next free index, which spreads colors over the whole palette. The result depends only on
 * the board, so a puzzle always looks the same. If the palette is ever too small for the
 * layout, the affected region falls back to its preferred color rather than failing.
 */
internal fun assignRegionColors(board: TecBoardUiModel, paletteSize: Int): Map<RegionId, Int> {
    require(paletteSize > 0) { "Palette must contain at least one color" }

    val regionAt = board.cells.associate { (it.cellId.row to it.cellId.column) to it.regionId }
    val neighbours = HashMap<RegionId, MutableSet<RegionId>>()

    for (cell in board.cells) {
        val adjacent = neighbours.getOrPut(cell.regionId) { mutableSetOf() }
        for ((rowStep, columnStep) in ORTHOGONAL_STEPS) {
            val other = regionAt[(cell.cellId.row + rowStep) to (cell.cellId.column + columnStep)]
            if (other != null && other != cell.regionId) adjacent += other
        }
    }

    val order = neighbours.keys.sortedWith(
        compareByDescending<RegionId> { neighbours.getValue(it).size }.thenBy { it.value }
    )

    val assigned = HashMap<RegionId, Int>()
    for (region in order) {
        val taken = neighbours.getValue(region).mapNotNull { assigned[it] }.toSet()
        val preferred = region.value % paletteSize
        assigned[region] = (0 until paletteSize)
            .map { offset -> (preferred + offset) % paletteSize }
            .firstOrNull { it !in taken }
            ?: preferred
    }
    return assigned
}

private val ORTHOGONAL_STEPS = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
