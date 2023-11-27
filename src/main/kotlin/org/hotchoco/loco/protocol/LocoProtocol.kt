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
        fun wrap(header: LocoHeader, body: JsonElement): LocoProtocol {
            return LocoProtocol(
                header = header,
                body = ByteBuffer.wrap(
                    Bson.serialize(
                        JsonElement.serializer(),
                        body
                    )
                )
            )
        }

        fun parse(data: ByteArray): LocoProtocol {
            require(data.size >= 21) { "Invalid data size" }

            // parse header
            val packetId = ByteBuffer.wrap(data.sliceArray(0..3)).int
            val status = ByteBuffer.wrap(data.sliceArray(4..5)).short
            val method = LocoMethod.wrap(data.sliceArray(6..16).toString(Charsets.UTF_8).replace("\u0000", ""))
            val bodyType = data[17]
            val bodyLength = ByteBuffer.wrap(data.sliceArray(18..21)).int

            println(data.sliceArray(18..21))

            // parse body
            val body = ByteBuffer.wrap(data.sliceArray(22..(22 + bodyLength)))

            println(body)

            println(data.size)

            return LocoProtocol(
                header = LocoHeader(
                    packetId = packetId,
                    status = status,
                    method = method
                ),
                body = body
            )
        }
    }

    fun toByteArray(): ByteArray {
        val headerBytes = ByteBuffer.allocate(22)
        headerBytes.putInt(header.packetId)
        headerBytes.putShort(header.status)
        headerBytes.put(header.method.methodBytes)
        headerBytes.put(0)
        headerBytes.putInt(body.capacity())
        println("test: ${body.capacity()}")
        headerBytes.flip()

        val data = ByteArray(22 + body.capacity())
        headerBytes.get(data, 0, 21)
        body.get(data, 22, body.capacity())

        return data
    }
}
