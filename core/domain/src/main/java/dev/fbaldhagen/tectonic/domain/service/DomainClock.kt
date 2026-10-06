package dev.fbaldhagen.tectonic.domain.service

import kotlin.time.Instant

fun interface DomainClock {
    fun now(): Instant
}