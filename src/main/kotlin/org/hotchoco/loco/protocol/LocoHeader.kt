package org.hotchoco.loco.protocol

data class LocoHeader(
    val packetId: Int,
    val status: Short,
    val method: LocoMethod
)