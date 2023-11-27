package org.hotchoco.loco.protocol

import kotlinx.serialization.json.JsonElement
import java.nio.ByteBuffer

/**
 * (header)
 * 4 bytes - packet id
 * 2 bytes - status
 * 11 bytes - method
 * 1 byte - body type
 * 4 bytes - body length
 * (body)
 * n bytes - body
 */
data class LocoProtocol(
    val header: LocoHeader,
    val body: ByteBuffer
) {
    companion object {
        fun wrap(header: LocoHeader, body: JsonElement): LocoProtocol = LocoProtocol(
            header = header,
            body = ByteBuffer.wrap(
                Bson.serialize(
                    JsonElement.serializer(),
                    body
                )
            )
        )

        fun parse(data: ByteArray): LocoProtocol {
            require(data.size >= 21) { "Invalid data size" }

            val buffer = ByteBuffer.wrap(data)

            // Parse header
            val packetId = buffer.int
            val status = buffer.short
            val methodBytes = ByteArray(11)
            buffer.get(methodBytes)
            val method = methodBytes.decodeToString().replace("\u0000", "")
            val bodyType = buffer.get()
            val bodyLength = buffer.int

            require(data.size >= 21 + bodyLength) { "Data should have enough bytes for the specified body length." }

            val body = ByteBuffer.allocate(bodyLength)
            buffer.get(body.array())

            return LocoProtocol(
                header = LocoHeader(
                    packetId,
                    status,
                    LocoMethod.wrap(method)
                ),
                body = body
            )
        }
    }

    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(22 + body.capacity())

        buffer.putInt(header.packetId)
        buffer.putShort(header.status)
        buffer.put(header.method.methodBytes)
        buffer.put(0.toByte())
        buffer.putInt(body.capacity())

        buffer.put(body)

        return buffer.array()
    }
}
