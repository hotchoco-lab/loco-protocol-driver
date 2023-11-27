package org.hotchoco.loco.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import org.bson.BasicBSONEncoder
import org.bson.BasicBSONObject
import org.bson.BsonBinaryReader
import org.bson.codecs.DecoderContext
import org.bson.codecs.DocumentCodec
import java.nio.ByteBuffer

object Bson {

    val codec = DocumentCodec()

    fun <T> serialize(serializer: KSerializer<T>, data: T): ByteArray {
        val bsonObject = BasicBSONObject()
        val jsonElement = Json.encodeToJsonElement(serializer, data)
        jsonElement.jsonObject.forEach { key, value ->
            bsonObject[key] = value.toString()
        }
        val bsonEncoder = BasicBSONEncoder()
        return bsonEncoder.encode(bsonObject)
    }

    fun <T> deserialize(serializer: KSerializer<T>, data: ByteArray): T {
        val reader = BsonBinaryReader(ByteBuffer.wrap(data))
        val document = codec.decode(reader, DecoderContext.builder().build())
        reader.close()

        return Json.decodeFromJsonElement(
            serializer,
            document.toMap().toJsonElement()
        )
    }

}

fun Collection<*>.toJsonElement(): JsonElement = JsonArray(mapNotNull { it.toJsonElement() })

fun Map<*, *>.toJsonElement(): JsonElement = JsonObject(
    mapNotNull {
        (it.key as? String ?: return@mapNotNull null) to it.value.toJsonElement()
    }.toMap(),
)

fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is Map<*, *> -> toJsonElement()
    is Collection<*> -> toJsonElement()
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    else -> JsonPrimitive(toString())
}