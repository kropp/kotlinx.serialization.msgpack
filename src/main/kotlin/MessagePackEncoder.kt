import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.io.*

class MessagePackEncoder(private val stream: OutputStream) : AbstractEncoder() {
  override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
    stream.write(bytes(0x80 + descriptor.elementsCount))
    return this
  }

  override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
    if (descriptor.kind != StructureKind.LIST && descriptor.kind != StructureKind.MAP) {
      encodeString(descriptor.getElementName(index))
    }
    return true
  }

  override fun endStructure(descriptor: SerialDescriptor) {}

  override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
    stream.write(when (descriptor.kind) {
      StructureKind.MAP -> bytes(0x80 + collectionSize)
      StructureKind.LIST -> when {
        collectionSize < 16       -> bytes(0x90 + collectionSize)
        collectionSize < 256      -> bytes(0xdc, 0, collectionSize)
        collectionSize < 65536    -> bytes(0xdc) + collectionSize.toByteArray().take(2).toByteArray()
        collectionSize < 16777216 -> bytes(0xdd, 0) + collectionSize.toByteArray().take(3).toByteArray()
        else                      -> bytes(0xdd) + collectionSize.toByteArray().take(4).toByteArray()
      }
      else -> bytes()
    })

    return this
  }

  override fun encodeString(value: String) {
    val utf8Bytes = value.toByteArray(Charsets.UTF_8)
    stream.write(when {
      utf8Bytes.size <    32 -> ByteArray(1) { (0xa0 + utf8Bytes.size).toByte() }
      utf8Bytes.size <   256 -> bytes(0xd9) + utf8Bytes.size.toByte()
      utf8Bytes.size < 65536 -> bytes(0xda) + utf8Bytes.size.toByteArray().take(2).toByteArray()
      else                   -> bytes(0xdb) + utf8Bytes.size.toByteArray()
    })
    stream.write(utf8Bytes)
  }

  override fun encodeBoolean(value: Boolean) {
    stream.write(bytes(if (value) 0xc3 else 0xc2))
  }

  override fun encodeByte(value: Byte) {
    stream.write(ByteArray(1) { value })
  }

  override fun encodeShort(value: Short) {
    if (value <= Byte.MAX_VALUE) return encodeByte(value.toByte())
    stream.write(0xd1)
    stream.write(value.toByteArray())
  }

  override fun encodeInt(value: Int) {
    if (value <= Byte.MAX_VALUE) return encodeByte(value.toByte())
    if (value <= Short.MAX_VALUE) return encodeShort(value.toShort())
    stream.write(0xd2)
    stream.write(value.toByteArray())
  }

  override fun encodeLong(value: Long) {
    if (value <= Byte.MAX_VALUE) return encodeByte(value.toByte())
    if (value <= Short.MAX_VALUE) return encodeShort(value.toShort())
    if (value <= Int.MAX_VALUE) return encodeInt(value.toInt())
    stream.write(0xd3)
    stream.write(value.toByteArray())
  }

  override fun encodeFloat(value: Float) {
    stream.write(bytes(0xca) + value.toBits().toByteArray())
  }

  override fun encodeDouble(value: Double) {
    stream.write(bytes(0xcb) + value.toBits().toByteArray())
  }

  override fun encodeNull() {
    stream.write(bytes(0xc0))
  }

  override fun encodeValue(value: Any) {
    if (value is ByteArray) {
      stream.write(when {
        value.size <   256 -> bytes(0xc4) + value.size.toByte()
        value.size < 65536 -> bytes(0xc5) + value.size.toByteArray().take(2).toByteArray()
        else               -> bytes(0xc6) + value.size.toByteArray()
      })
      stream.write(value)
    } else {
      super.encodeValue(value)
    }
  }
}