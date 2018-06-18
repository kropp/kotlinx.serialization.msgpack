import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import java.io.*

class MessagePack {
  fun <T> parse(bytes: ByteArray, serial: KSerialLoader<T>): T {
    return serial.load(MessagePackInput(bytes))
  }

  fun <T> pack(obj: T, serial: KSerialSaver<T>): ByteArray {
    val output = MessagePackOutput()
    serial.save(output, obj)
    return output.bytes
  }

  companion object {
    fun <T> parse(bytes: ByteArray, serial: KSerialLoader<T>) = MessagePack().parse(bytes, serial)
    inline fun <reified T : Any> parse(bytes: ByteArray) = MessagePack().parse(bytes, T::class.serializer())

    fun <T> pack(obj: T, serial: KSerialSaver<T>): ByteArray = MessagePack().pack(obj, serial)
    inline fun <reified T : Any> pack(obj: T): ByteArray = MessagePack().pack(obj, T::class.serializer())
  }
}

class MessagePackOutput(initial: ByteArray = ByteArray(0)) : NamedValueOutput() {
  internal var bytes: ByteArray = initial
    private set

  override fun writeBegin(desc: KSerialClassDesc, vararg typeParams: KSerializer<*>): KOutput {
    bytes += byteArray(0x80 + desc.associatedFieldsCount)
    return this
  }

  override fun writeTaggedString(tag: String, value: String) {
    bytes += byteArray(0xa0 + tag.length, tag, 0xa0 + value.length, value)
  }

  override fun writeTaggedBoolean(tag: String, value: Boolean) {
    bytes += byteArray(0xa0 + tag.length, tag, if (value) 0xc3 else 0xc2)
  }

  override fun writeTaggedInt(tag: String, value: Int) {
    bytes += byteArray(0xa0 + tag.length, tag, 0x00)
  }

  override fun writeTaggedFloat(tag: String, value: Float) {
    bytes += byteArray(0xa0 + tag.length, tag, 0xca) + value.toBits().toByteArray()
  }

  override fun writeTaggedDouble(tag: String, value: Double) {
    bytes += byteArray(0xa0 + tag.length, tag, 0xcb) + value.toBits().toByteArray()
  }

  override fun writeTaggedNull(tag: String) {
    bytes += byteArray(0xa0 + tag.length, tag, 0xc0)
  }
}

class MessagePackInput(bytes: ByteArray) : NamedValueInput() {
  private val byteStream = ByteArrayInputStream(bytes)
  private val map = mutableMapOf<String,Any>()
  private val nulls = mutableSetOf<String>()

  override fun readBegin(desc: KSerialClassDesc, vararg typeParams: KSerializer<*>): KInput {
    val reader = MessagePackBinaryReader(byteStream)

    val count = byteStream.read() - 0x80
    repeat(count) {
      val name = reader.readString()
      val value = reader.readNext()
      if (value != null) {
        map[name] = value
      } else {
        nulls += name
      }
    }

    return this
  }

  override fun readTaggedValue(tag: String): Any {
    return map[tag]!!
  }

  override fun readTaggedNotNullMark(tag: String) = tag !in nulls
}

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
