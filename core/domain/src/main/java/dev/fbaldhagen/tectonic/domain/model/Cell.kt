package dev.fbaldhagen.tectonic.domain.model

data class Cell(
    val id: CellId,
    val regionId: RegionId,
    val givenValue: Int?
)