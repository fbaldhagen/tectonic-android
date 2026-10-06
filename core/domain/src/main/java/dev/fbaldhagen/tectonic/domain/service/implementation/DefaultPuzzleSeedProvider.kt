package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.service.PuzzleSeedProvider
import kotlin.random.Random

class DefaultPuzzleSeedProvider(
    private val random: Random = Random.Default
) : PuzzleSeedProvider {
    override fun nextSeed(): Long = random.nextLong()
}