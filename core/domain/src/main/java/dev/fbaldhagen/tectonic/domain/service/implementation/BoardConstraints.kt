package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.Puzzle

/** Index-based view of the static rules of a puzzle (regions and neighborhoods). */
internal class BoardConstraints(
    val topology: BoardTopology,
    val regionOf: IntArray,
    val regionCells: Array<IntArray>
) {
    val cellCount: Int get() = topology.cellCount

    init {
        require(regionCells.all { it.size <= MAX_REGION_SIZE }) {
            "Regions larger than $MAX_REGION_SIZE cells are not supported"
        }
    }

    fun indexOf(cellId: CellId): Int = cellId.row * topology.columns + cellId.column

    fun cellIdOf(index: Int): CellId = CellId(index / topology.columns, index % topology.columns)

    fun givensOf(puzzle: Puzzle): IntArray {
        val givens = IntArray(cellCount)
        puzzle.cells.forEach { cell -> givens[indexOf(cell.id)] = cell.givenValue ?: 0 }
        return givens
    }

    companion object {
        const val MAX_REGION_SIZE = 30

        fun from(puzzle: Puzzle): BoardConstraints {
            val topology = BoardTopology.of(puzzle.gridSize)
            val regionIndexById = puzzle.regions.mapIndexed { index, region -> region.id to index }.toMap()
            val regionOf = IntArray(topology.cellCount)
            puzzle.cells.forEach { cell ->
                val index = cell.id.row * topology.columns + cell.id.column
                regionOf[index] = regionIndexById.getValue(cell.regionId)
            }
            val regionCells = Array(puzzle.regions.size) { regionIndex ->
                puzzle.regions[regionIndex].cells
                    .map { it.row * topology.columns + it.column }
                    .toIntArray()
            }
            return BoardConstraints(topology, regionOf, regionCells)
        }
    }
}
