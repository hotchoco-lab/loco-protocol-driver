package org.hotchoco.loco.protocol

class LocoMethod private constructor(
    private val method: String
) {

    companion object {
        fun wrap(method: String): LocoMethod = LocoMethod(method)
    }

    val methodBytes: ByteArray
        get() {
            val bytes = ByteArray(11)
            val methodBytes = method.toByteArray()
            System.arraycopy(methodBytes, 0, bytes, 0, methodBytes.size)
            return bytes
        }

    val methodName: String
        get() = method

    override fun toString(): String {
        return method
    }

}