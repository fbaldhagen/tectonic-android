package dev.fbaldhagen.tectonic.domain.model

data class Region(
    val id: RegionId,
    val cells: Set<CellId>
)