package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.model.CellId
import dev.fbaldhagen.tectonic.domain.model.GridSize
import dev.fbaldhagen.tectonic.domain.model.MoveValidation
import dev.fbaldhagen.tectonic.domain.model.MoveViolation
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleState
import dev.fbaldhagen.tectonic.domain.service.MoveValidator

class DefaultMoveValidator : MoveValidator {

    override fun validate(
        puzzle: Puzzle,
        state: PuzzleState,
        cellId: CellId,
        value: Int
    ): MoveValidation {
        val violations = mutableSetOf<MoveViolation>()

        val cell = puzzle.cells.find { it.id == cellId }
            ?: return MoveValidation(isValid = false, violations = setOf(MoveViolation.CELL_NOT_EDITABLE))

        // 1. Editable?
        if (cell.givenValue != null) {
            violations += MoveViolation.CELL_NOT_EDITABLE
            return MoveValidation(isValid = false, violations = violations)
        }

        val region = puzzle.regions.find { it.id == cell.regionId }
            ?: return MoveValidation(isValid = false, violations = setOf(MoveViolation.VALUE_OUT_OF_RANGE))

        val regionSize = region.cells.size

        // 2. Range
        if (value !in 1..regionSize) {
            violations += MoveViolation.VALUE_OUT_OF_RANGE
        }

        // Current board = state values (givens should already be present in state)
        val currentValues = state.values

        // 3. Region conflict
        val hasRegionConflict = region.cells.any { otherId ->
            otherId != cellId && currentValues[otherId] == value
        }
        if (hasRegionConflict) {
            violations += MoveViolation.REGION_CONFLICT
        }

        // 4. Adjacency conflict (8 directions)
        val hasAdjacencyConflict = getNeighbors(cellId, puzzle.gridSize).any { neighborId ->
            currentValues[neighborId] == value
        }
        if (hasAdjacencyConflict) {
            violations += MoveViolation.ADJACENCY_CONFLICT
        }

        return MoveValidation(
            isValid = violations.isEmpty(),
            violations = violations
        )
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