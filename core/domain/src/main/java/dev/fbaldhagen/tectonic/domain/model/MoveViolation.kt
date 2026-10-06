package dev.fbaldhagen.tectonic.domain.model

enum class MoveViolation {
    CELL_NOT_EDITABLE,
    VALUE_OUT_OF_RANGE,
    REGION_CONFLICT,
    ADJACENCY_CONFLICT
}