import kotlinx.serialization.*
import java.io.*

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