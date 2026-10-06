package dev.fbaldhagen.tectonic.domain.model

data class SolutionCheckResult(
    val status: SolutionStatus,
    val incorrectCells: Set<CellId>
)