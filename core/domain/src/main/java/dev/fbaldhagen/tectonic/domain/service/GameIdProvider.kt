package dev.fbaldhagen.tectonic.domain.service

import dev.fbaldhagen.tectonic.domain.model.GameId

fun interface GameIdProvider {
    fun nextId(): GameId
}