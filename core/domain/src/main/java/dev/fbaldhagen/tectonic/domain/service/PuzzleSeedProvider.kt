package dev.fbaldhagen.tectonic.domain.service

fun interface PuzzleSeedProvider {
    fun nextSeed(): Long
}