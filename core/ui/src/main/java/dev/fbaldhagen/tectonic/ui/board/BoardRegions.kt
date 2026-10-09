package dev.fbaldhagen.tectonic.ui.board

/** Position-indexed region ids, so cell borders can be decided without searching the cell list. */
internal class BoardRegions(
    private val rows: Int,
    private val columns: Int,
    private val regionIds: IntArray,
) {
    fun isInRegion(row: Int, column: Int, regionId: Int): Boolean =
        row in 0 until rows &&
            column in 0 until columns &&
            regionIds[row * columns + column] == regionId

    companion object {
        fun of(board: TecBoardUiModel): BoardRegions {
            val ids = IntArray(board.rows * board.columns) { NO_REGION }
            board.cells.forEach { cell ->
                ids[cell.cellId.row * board.columns + cell.cellId.column] = cell.regionId.value
            }
            return BoardRegions(board.rows, board.columns, ids)
        }

        private const val NO_REGION = -1
    }
}
