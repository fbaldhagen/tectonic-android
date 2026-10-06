package dev.fbaldhagen.tectonic.domain.model

data class MoveValidation(
    val isValid: Boolean,
    val violations: Set<MoveViolation>
)