import kotlinx.serialization.*
import java.io.*

class MessagePackInput(bytes: ByteArray) : TaggedDecoder<String>() {
  override fun SerialDescriptor.getTag(index: Int) = getElementAnnotations(index).filterIsInstance<SerialTag>().firstOrNull()?.tag ?: getElementName(index)

  private val byteStream = ByteArrayInputStream(bytes)
  private val map = mutableMapOf<String,Any>()
  private val nulls = mutableSetOf<String>()

  override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
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

  override fun decodeTaggedValue(tag: String): Any {
    return map[tag]!!
  }

  override fun decodeTaggedByte(tag: String): Byte {
    val v = decodeTaggedValue(tag)
    return when (v) {
      is Byte -> v
      is Int -> v.toByte()
      is Long -> v.toByte()
      else -> v as Byte
    }
  }

  override fun decodeTaggedLong(tag: String): Long {
    val v = decodeTaggedValue(tag)
    return when (v) {
      is Byte -> v.toLong()
      is Int -> v.toLong()
      is Long -> v
      else -> v as Long
    }
  }

  override fun decodeCollectionSize(desc: SerialDescriptor): Int {
    return (decodeTaggedValue(currentTag) as? Array<*>)?.size ?: 0
  }

  override fun decodeTaggedNotNullMark(tag: String) = tag !in nulls
}