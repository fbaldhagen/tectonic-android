package dev.fbaldhagen.tectonic.domain.service.implementation

import kotlin.random.Random

/**
 * Builds regions and their values together with backtracking, so every produced layout
 * is solvable by construction. Generating a layout first and solving it afterward
 * fails on a large share of random layouts.
 */
internal class ConstructivePuzzleBuilder(
    private val topology: BoardTopology,
    private val random: Random,
    private val maxNodes: Int
) {
    private val cellCount = topology.cellCount
    private val board = IntArray(cellCount)
    private val regionOf = IntArray(cellCount) { UNASSIGNED }
    private val regions = ArrayList<IntArray>()
    private var nodes = 0

    /** Returns null when the node budget is exhausted before a board is found. */
    fun build(): ConstructedBoard? {
        board.fill(0)
        regionOf.fill(UNASSIGNED)
        regions.clear()
        nodes = 0

        return if (placeRegions()) ConstructedBoard(regions.toList(), board.copyOf()) else null
    }

    private fun placeRegions(): Boolean {
        if (++nodes > maxNodes) return false

        val start = firstUnassignedCell()
        if (start == -1) return true

        for (size in REGION_SIZE_POOL.shuffled(random)) {
            val shapes = if (size == 1) listOf(intArrayOf(start)) else growShapes(start, size)
            for (cells in shapes) {
                if (!assignValues(cells, size)) continue

                val regionIndex = regions.size
                regions += cells
                cells.forEach { regionOf[it] = regionIndex }

                if (placeRegions()) return true

                cells.forEach {
                    regionOf[it] = UNASSIGNED
                    board[it] = 0
                }
                regions.removeAt(regions.lastIndex)
            }
        }
        return false
    }

    private fun firstUnassignedCell(): Int = regionOf.indexOfFirst { it == UNASSIGNED }

    /** Up to [SHAPES_PER_SIZE] distinct connected shapes of exactly [target] cells containing [start]. */
    private fun growShapes(start: Int, target: Int): List<IntArray> {
        val found = LinkedHashMap<List<Int>, IntArray>()
        val shape = ArrayList<Int>().apply { add(start) }
        val inShape = BooleanArray(cellCount).also { it[start] = true }

        fun extend() {
            if (found.size >= SHAPES_PER_SIZE) return
            if (shape.size == target) {
                found.putIfAbsent(shape.sorted(), shape.toIntArray())
                return
            }
            val frontier = LinkedHashSet<Int>()
            for (cell in shape) {
                for (neighbour in topology.orthogonal[cell]) {
                    if (regionOf[neighbour] == UNASSIGNED && !inShape[neighbour]) frontier += neighbour
                }
            }
            for (next in frontier.shuffled(random)) {
                shape += next
                inShape[next] = true
                extend()
                shape.removeAt(shape.lastIndex)
                inShape[next] = false
                if (found.size >= SHAPES_PER_SIZE) return
            }
        }

        extend()
        return found.values.toList()
    }

    /** Fills [cells] with 1..[size] so that no value touches an equal value. */
    private fun assignValues(cells: IntArray, size: Int): Boolean {
        val order = cells.toList().shuffled(random)
        val valueOrder = (1..size).toList()
        val used = BooleanArray(size + 1)

        fun fill(position: Int): Boolean {
            if (position == order.size) return true
            val cell = order[position]
            for (value in valueOrder.shuffled(random)) {
                if (used[value] || touchesValue(cell, value)) continue
                board[cell] = value
                used[value] = true
                if (fill(position + 1)) return true
                used[value] = false
                board[cell] = 0
            }
            return false
        }

        if (fill(0)) return true
        cells.forEach { board[it] = 0 }
        return false
    }

    private fun touchesValue(cell: Int, value: Int): Boolean =
        topology.surrounding[cell].any { board[it] == value }

    private companion object {
        const val UNASSIGNED = -1
        const val SHAPES_PER_SIZE = 8

        /**
         * Each region holds one 1 and equal values may not touch, so 1s form an independent
         * set on the king graph and regions are forced to be large on bigger boards. This mix
         * (repeated entries weight a size) was the most reliable of the ones measured.
         */
        val REGION_SIZE_POOL = listOf(2, 3, 4, 1, 5, 3, 2)
    }
}
