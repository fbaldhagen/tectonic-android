package dev.fbaldhagen.tectonic.domain.service.implementation

/**
 * Constraint-propagation solver on flat int arrays.
 *
 * Each empty cell carries a bitmask of the values it can still take. Placing a value
 * prunes the masks of its region and its eight neighbors, and propagation then applies
 * two logical rules until nothing changes:
 *  - naked single: a cell with exactly one candidate left
 *  - hidden single: a value that fits only one cell of its region
 *
 * Branching (copy-on-branch, most constrained cell first) is only used when logic alone
 * is not enough, which keeps uniqueness checks cheap.
 */
internal class LogicalBoardSolver(
    private val constraints: BoardConstraints,
    private val givens: IntArray
) {
    private val cellCount = constraints.cellCount
    private val surrounding = constraints.topology.surrounding

    private class State(val values: IntArray, val masks: IntArray) {
        fun copy() = State(values.copyOf(), masks.copyOf())
    }

    /** True when the givens can be completed using only naked and hidden singles. */
    fun solvesByLogicAlone(): Boolean {
        val state = initialState() ?: return false
        return propagate(state) && state.values.all { it != 0 }
    }

    /** Collects up to [limit] solutions, each as a flat value array. */
    fun findSolutions(limit: Int): List<IntArray> {
        val solutions = ArrayList<IntArray>()
        val state = initialState() ?: return solutions
        search(state, limit, solutions)
        return solutions
    }

    private fun search(state: State, limit: Int, solutions: MutableList<IntArray>) {
        if (!propagate(state)) return

        val branchCell = mostConstrainedEmptyCell(state)
        if (branchCell == -1) {
            solutions += state.values.copyOf()
            return
        }

        var remaining = state.masks[branchCell]
        while (remaining != 0 && solutions.size < limit) {
            val bit = remaining and -remaining
            remaining = remaining xor bit
            val branch = state.copy()
            if (assign(branch, branchCell, Integer.numberOfTrailingZeros(bit) + 1)) {
                search(branch, limit, solutions)
            }
        }
    }

    private fun initialState(): State? {
        val values = IntArray(cellCount)
        val masks = IntArray(cellCount) { cell -> fullMask(constraints.regionCells[constraints.regionOf[cell]].size) }
        val state = State(values, masks)
        for (cell in 0 until cellCount) {
            val given = givens[cell]
            if (given == 0) continue
            val regionSize = constraints.regionCells[constraints.regionOf[cell]].size
            if (given !in 1..regionSize) return null
            if (!assign(state, cell, given)) return null
        }
        return state
    }

    /** Places [value] and prunes peers. Returns false on a contradiction. */
    private fun assign(state: State, cell: Int, value: Int): Boolean {
        val bit = 1 shl (value - 1)
        if (state.values[cell] != 0 || state.masks[cell] and bit == 0) return false

        state.values[cell] = value
        state.masks[cell] = 0

        for (peer in constraints.regionCells[constraints.regionOf[cell]]) {
            if (!eliminate(state, peer, bit)) return false
        }
        for (peer in surrounding[cell]) {
            if (!eliminate(state, peer, bit)) return false
        }
        return true
    }

    private fun eliminate(state: State, cell: Int, bit: Int): Boolean {
        if (state.values[cell] != 0) return true
        val mask = state.masks[cell] and bit.inv()
        state.masks[cell] = mask
        return mask != 0
    }

    private fun propagate(state: State): Boolean {
        var changed = true
        while (changed) {
            changed = false

            for (cell in 0 until cellCount) {
                if (state.values[cell] != 0) continue
                val mask = state.masks[cell]
                if (mask == 0) return false
                if (mask and (mask - 1) == 0) {
                    if (!assign(state, cell, Integer.numberOfTrailingZeros(mask) + 1)) return false
                    changed = true
                }
            }

            for (region in constraints.regionCells) {
                val placed = placedMask(state, region)
                for (value in 1..region.size) {
                    val bit = 1 shl (value - 1)
                    if (placed and bit != 0) continue
                    var onlyCell = -1
                    var count = 0
                    for (cell in region) {
                        if (state.masks[cell] and bit != 0) {
                            onlyCell = cell
                            count++
                        }
                    }
                    if (count == 0) return false
                    if (count == 1) {
                        if (!assign(state, onlyCell, value)) return false
                        changed = true
                    }
                }
            }
        }
        return true
    }

    private fun placedMask(state: State, region: IntArray): Int {
        var placed = 0
        for (cell in region) {
            val value = state.values[cell]
            if (value != 0) placed = placed or (1 shl (value - 1))
        }
        return placed
    }

    private fun mostConstrainedEmptyCell(state: State): Int {
        var best = -1
        var bestCount = Int.MAX_VALUE
        for (cell in 0 until cellCount) {
            if (state.values[cell] != 0) continue
            val count = Integer.bitCount(state.masks[cell])
            if (count < bestCount) {
                bestCount = count
                best = cell
            }
        }
        return best
    }

    private fun fullMask(size: Int): Int = (1 shl size) - 1
}
