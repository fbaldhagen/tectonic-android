package dev.fbaldhagen.tectonic.domain.service.implementation

import dev.fbaldhagen.tectonic.domain.service.DomainClock
import kotlin.time.Clock
import kotlin.time.Instant

class DefaultDomainClock : DomainClock {
    override fun now(): Instant = Clock.System.now()
}