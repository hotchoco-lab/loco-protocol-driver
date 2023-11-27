import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.hotchoco.loco.protocol.LocoHeader
import org.hotchoco.loco.protocol.LocoMethod
import org.hotchoco.loco.protocol.LocoProtocol
import kotlin.test.Test

class ProtocolTest {

    @Test
    fun testLocoMethod() {
        val method = LocoMethod.wrap("LOCO")
        println(method.methodName)
        println(method.methodBytes)

        assert(method.methodName == "LOCO")
        assert(method.methodBytes.size == 11)
    }

    @Test
    fun testLocoHeader() {
        val header = LocoHeader(
            packetId = 1,
            status = 0,
            method = LocoMethod.wrap("WRITE")
        )

        println(header)
    }

    @Test
    fun testLocoProtocol() {
        val protocol = LocoProtocol(
            header = LocoHeader(
                packetId = 1,
                status = 0,
                method = LocoMethod.wrap("WRITE")
            ),
            body = Json.encodeToJsonElement(
                WriteRequest.serializer(),
                WriteRequest(
                    message = "Hello, World!"
                )
            )
        )

        println(protocol)
    }

}

@Serializable
data class WriteRequest(
    val message: String
)