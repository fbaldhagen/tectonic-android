package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.service.PuzzleValidator

class DefaultPuzzleValidator : PuzzleValidator {

    override fun validate(puzzle: Puzzle): TecResult<Unit> {
        val errors = mutableListOf<String>()

        // 1. Basic grid sanity
        if (puzzle.gridSize.rows <= 0 || puzzle.gridSize.columns <= 0) {
            errors += "Grid size must be positive"
        }

        val expectedCellCount = puzzle.gridSize.rows * puzzle.gridSize.columns
        if (puzzle.cells.size != expectedCellCount) {
            errors += "Expected $expectedCellCount cells, found ${puzzle.cells.size}"
        }

        // 2. Unique cell IDs and coverage of the whole grid
        val cellIds = puzzle.cells.map { it.id }.toSet()
        if (cellIds.size != puzzle.cells.size) {
            errors += "Duplicate CellIds found"
        }

        for (row in 0 until puzzle.gridSize.rows) {
            for (col in 0 until puzzle.gridSize.columns) {
                val id = CellId(row, col)
                if (id !in cellIds) {
                    errors += "Missing cell at ($row, $col)"
                }
            }
        }

        // 3. Regions
        if (puzzle.regions.isEmpty()) {
            errors += "Puzzle must contain at least one region"
        }

        val regionIds = puzzle.regions.map { it.id }.toSet()
        if (regionIds.size != puzzle.regions.size) {
            errors += "Duplicate RegionIds found"
        }

        val cellsByRegion = puzzle.cells.groupBy { it.regionId }

        for (region in puzzle.regions) {
            val regionCells = cellsByRegion[region.id].orEmpty()

            if (region.cells.size != regionCells.size) {
                errors += "Region ${region.id.value} declares ${region.cells.size} cells but ${regionCells.size} cells reference it"
            }

            if (region.cells != regionCells.map { it.id }.toSet()) {
                errors += "Region ${region.id.value} cell set does not match cells that reference it"
            }

            if (!isOrthogonallyConnected(region.cells)) {
                errors += "Region ${region.id.value} is not a connected shape"
            }
        }

        // Every cell must belong to a known region
        for (cell in puzzle.cells) {
            if (cell.regionId !in regionIds) {
                errors += "Cell ${cell.id} references unknown region ${cell.regionId.value}"
            }
        }

        // 4. Givens
        for (cell in puzzle.cells) {
            val given = cell.givenValue ?: continue
            val regionSize = cellsByRegion[cell.regionId]?.size ?: 0

            if (given !in 1..regionSize) {
                errors += "Given value $given at ${cell.id} is out of range for its region (1..$regionSize)"
            }
        }

        // 5. Solution map
        if (puzzle.solution.size != expectedCellCount) {
            errors += "Solution map size (${puzzle.solution.size}) does not match cell count ($expectedCellCount)"
        }

        for (cell in puzzle.cells) {
            val solutionValue = puzzle.solution[cell.id]
            if (solutionValue == null) {
                errors += "Missing solution value for cell ${cell.id}"
                continue
            }

            val regionSize = cellsByRegion[cell.regionId]?.size ?: 0
            if (solutionValue !in 1..regionSize) {
                errors += "Solution value $solutionValue at ${cell.id} is out of range for its region"
            }

            // Given must match solution
            if (cell.givenValue != null && cell.givenValue != solutionValue) {
                errors += "Given value at ${cell.id} does not match solution"
            }
        }

        // 6. Solution must obey the rules (region uniqueness + adjacency)
        val solutionErrors = validateSolutionRules(puzzle)
        errors += solutionErrors

        return if (errors.isEmpty()) {
            TecResult.Success(Unit)
        } else {
            TecResult.Error(
                AppError.Unknown(
                    message = "Invalid puzzle: ${errors.joinToString("; ")}"
                )
            )
        }
    }

    private fun validateSolutionRules(puzzle: Puzzle): List<String> {
        val errors = mutableListOf<String>()
        val solution = puzzle.solution

        // Region uniqueness
        for (region in puzzle.regions) {
            val values = region.cells.mapNotNull { solution[it] }
            if (values.size != region.cells.size) {
                errors += "Region ${region.id.value} has incomplete solution values"
                continue
            }
            if (values.toSet().size != values.size) {
                errors += "Region ${region.id.value} contains duplicate values in solution"
            }
            val expected = (1..region.cells.size).toSet()
            if (values.toSet() != expected) {
                errors += "Region ${region.id.value} solution does not contain exactly 1..${region.cells.size}"
            }
        }

        // Adjacency (8 directions)
        for (cell in puzzle.cells) {
            val value = solution[cell.id] ?: continue
            val neighbors = getNeighbors(cell.id, puzzle.gridSize)
            for (neighbor in neighbors) {
                if (solution[neighbor] == value) {
                    errors += "Solution has adjacent equal values at ${cell.id} and $neighbor (value $value)"
                    // No need to report every pair multiple times; one is enough for the error list
                    break
                }
            }
        }

        return errors
    }

    private fun isOrthogonallyConnected(cells: Set<CellId>): Boolean {
        if (cells.isEmpty()) return true
        val visited = mutableSetOf(cells.first())
        val queue = ArrayDeque(visited)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val next = listOf(
                CellId(current.row - 1, current.column),
                CellId(current.row + 1, current.column),
                CellId(current.row, current.column - 1),
                CellId(current.row, current.column + 1)
            )
            for (candidate in next) {
                if (candidate in cells && visited.add(candidate)) queue.addLast(candidate)
            }
        }
        return visited.size == cells.size
    }

    private fun getNeighbors(cellId: CellId, gridSize: GridSize): List<CellId> {
        val neighbors = mutableListOf<CellId>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val r = cellId.row + dr
                val c = cellId.column + dc
                if (r in 0 until gridSize.rows && c in 0 until gridSize.columns) {
                    neighbors += CellId(r, c)
                }
            }
        }
        return neighbors
    }
}