package org.hotchoco.loco.protocol

import kotlinx.serialization.json.JsonElement

data class LocoProtocol(
    val header: LocoHeader,
    val body: JsonElement
)
