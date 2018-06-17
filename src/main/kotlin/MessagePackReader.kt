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

  override fun writeTaggedBoolean(tag: String, value: Boolean) {
    bytes += byteArray(0xa0 + tag.length, tag, if (value) 0xc3 else 0xc2)
  }

  override fun writeTaggedInt(tag: String, value: Int) {
    bytes += byteArray(0xa0 + tag.length, tag, 0x00)
  }
}

class MessagePackInput(bytes: ByteArray) : NamedValueInput() {
  private val byteStream = ByteArrayInputStream(bytes)
  private val map = mutableMapOf<String,Any>()

  override fun readBegin(desc: KSerialClassDesc, vararg typeParams: KSerializer<*>): KInput {
    val reader = MessagePackBinaryReader(byteStream)

    val count = byteStream.read() - 0x80
    repeat(count) {
      map[reader.readString()] = reader.readNext()
    }

    return this
  }

  override fun readTaggedValue(tag: String): Any {
    return map[tag]!!
  }
}

class MessagePackBinaryReader(private val stream: ByteArrayInputStream) {
  fun readString() = readNext() as String
  fun readNext(): Any {
    val type = stream.read()
    when (type) {
      0x00 -> return 0
      //0xc0 -> return null
      0xc2 -> return false
      0xc3 -> return true
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
