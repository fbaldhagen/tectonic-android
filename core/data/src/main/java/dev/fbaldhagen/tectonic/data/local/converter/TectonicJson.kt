package dev.fbaldhagen.tectonic.data.local.converter

import kotlinx.serialization.json.Json

internal val TectonicJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}