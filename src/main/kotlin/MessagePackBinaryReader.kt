import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import java.io.*

class MessagePackBinaryReader(private val stream: ByteArrayInputStream) {
  fun readString() = readNext() as String
  fun readNext(): Any? {
    val type = stream.read()
    when (type) {
      0x00 -> return 0
      0xc0 -> return null
      0xc2 -> return false
      0xc3 -> return true
      0xca -> return Float.fromBits(stream.readExactNBytes(4).toInt())
      0xcb -> return Double.fromBits(stream.readExactNBytes(8).toLong())
    }
    if (type and 0xa0 == 0xa0) {
      val length = type - 0xa0
      return String(stream.readExactNBytes(length))
    }
    throw IllegalStateException("Unexpected byte ${type.toString(16)}")
  }
}


fun byteArray(vararg data: Any) = data.map { d ->
  when(d) {
    is Int -> ByteArray(1) { d.toByte() }
    is String -> d.toUtf8Bytes()
    else -> ByteArray(0)
  }
}.fold(ByteArray(0)) { acc, it -> acc + it }

fun Int.toByteArray() = ByteArray(4) { ((this shr (7-it) * 8) and 0xFF).toByte() }
fun Long.toByteArray() = ByteArray(8) { ((this shr (7-it) * 8) and 0xFF).toByte() }

fun ByteArray.toInt() =
    (get(0).toInt() and 0xFF shl 24) +
    (get(1).toInt() and 0xFF shl 16) +
    (get(2).toInt() and 0xFF shl 8) +
    (get(3).toInt() and 0xFF)

fun ByteArray.toLong() =
    (get(0).toLong() and 0xFF shl 56) +
    (get(1).toLong() and 0xFF shl 48) +
    (get(2).toLong() and 0xFF shl 40) +
    (get(3).toLong() and 0xFF shl 32) +
    (get(4).toLong() and 0xFF shl 24) +
    (get(5).toLong() and 0xFF shl 16) +
    (get(6).toLong() and 0xFF shl 8) +
    (get(7).toLong() and 0xFF)
