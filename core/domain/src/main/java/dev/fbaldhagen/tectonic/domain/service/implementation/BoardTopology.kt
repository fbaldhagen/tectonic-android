package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.model.GridSize

/**
 * Precomputed neighborhoods for a grid. Cells are addressed by a flat index
 * (row * columns + column) so the hot paths of the solver and generator work on
 * plain int arrays instead of boxed ids.
 */
internal class BoardTopology(val rows: Int, val columns: Int) {

    val cellCount: Int = rows * columns

    /** Orthogonal neighbors, used for region connectivity. */
    val orthogonal: Array<IntArray> = buildNeighbours(ORTHOGONAL_OFFSETS)

    /** All eight neighbors, used for the "no equal values touching" rule. */
    val surrounding: Array<IntArray> = buildNeighbours(SURROUNDING_OFFSETS)

    private fun buildNeighbours(offsets: List<Pair<Int, Int>>): Array<IntArray> =
        Array(cellCount) { index ->
            val row = index / columns
            val column = index % columns
            offsets.mapNotNull { (dr, dc) ->
                val r = row + dr
                val c = column + dc
                if (r in 0 until rows && c in 0 until columns) r * columns + c else null
            }.toIntArray()
        }

    companion object {
        fun of(gridSize: GridSize) = BoardTopology(gridSize.rows, gridSize.columns)

        private val ORTHOGONAL_OFFSETS = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        private val SURROUNDING_OFFSETS = buildList {
            for (dr in -1..1) for (dc in -1..1) if (dr != 0 || dc != 0) add(dr to dc)
        }
    }
}
