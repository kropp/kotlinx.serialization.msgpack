import kotlinx.serialization.*
import java.io.*

// from kotlinx.serialization.internal
internal fun InputStream.readExactNBytes(bytes: Int): ByteArray {
  val array = ByteArray(bytes)
  var read = 0
  while (read < bytes) {
    val i = this.read(array, read, bytes - read)
    if (i == -1) throw IOException("Unexpected EOF")
    read += i
  }
  return array
}

class MessagePackBinaryReader(private val stream: InputStream) {
  fun readString() = readNext() as String
  fun readNext(): Any? {
    val type = stream.read()
    when (type) {
      0x00 -> return 0
      0xc0 -> return null
      0xc2 -> return false
      0xc3 -> return true
      0xc4 -> {
        val length = stream.readExactNBytes(1)[0].toInt()
        return stream.readExactNBytes(length)
      }
      0xc5 -> {
        val length = stream.readExactNBytes(2).toShort()
        return stream.readExactNBytes(length)
      }
      0xc6 -> {
        val length = stream.readExactNBytes(4).toInt()
        return stream.readExactNBytes(length)
      }
      0xca -> return Float.fromBits(stream.readExactNBytes(4).toInt())
      0xcb -> return Double.fromBits(stream.readExactNBytes(8).toLong())
      0xcc -> return stream.readExactNBytes(1)[0].toInt()
      0xcd -> return stream.readExactNBytes(2).toShort()
      0xce -> return stream.readExactNBytes(4).toInt()
      0xcf -> return stream.readExactNBytes(8).toLong()
      0xd0 -> return stream.readExactNBytes(1)[0].toInt()
      0xd1 -> return stream.readExactNBytes(2).toShort()
      0xd2 -> return stream.readExactNBytes(4).toInt()
      0xd3 -> return stream.readExactNBytes(8).toLong()
      0xd9 -> {
        val length = stream.readExactNBytes(1)[0].toInt()
        return String(stream.readExactNBytes(length))
      }
      0xda -> {
        val length = stream.readExactNBytes(2).toShort()
        return String(stream.readExactNBytes(length))
      }
      0xdb -> {
        val length = stream.readExactNBytes(4).toInt()
        return String(stream.readExactNBytes(length))
      }
    }
    return when {
      type or 0b01111111 == 0b01111111 -> type
      type and 0b11100000 == 0b11100000 -> type
      type and 0xa0 == 0xa0 -> {
        val length = type - 0xa0
        String(stream.readExactNBytes(length))
      }
      type and 0x90 == 0x90 -> {
        val size = type - 0x90
        Array(size) {
          readNext()
        }
      }
      else -> throw IllegalStateException("Unexpected byte ${type.toString(16)}")
    }
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

fun ByteArray.toShort() =
    (get(0).toInt() and 0xFF shl 8) +
    (get(1).toInt() and 0xFF)

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
