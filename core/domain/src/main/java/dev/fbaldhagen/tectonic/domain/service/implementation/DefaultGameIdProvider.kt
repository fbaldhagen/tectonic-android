package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.model.GameId
import dev.fbaldhagen.tectonic.domain.service.GameIdProvider
import java.util.UUID

class DefaultGameIdProvider : GameIdProvider {
    override fun nextId(): GameId = GameId(UUID.randomUUID().toString())
}