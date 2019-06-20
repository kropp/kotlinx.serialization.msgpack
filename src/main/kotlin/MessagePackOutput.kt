import kotlinx.serialization.*

class MessagePackOutput(initial: ByteArray = ByteArray(0)) : TaggedEncoder<String>() {
  override fun SerialDescriptor.getTag(index: Int) = getElementAnnotations(index).filterIsInstance<SerialTag>().firstOrNull()?.tag ?: getElementName(index)

  internal var bytes: ByteArray = initial
    private set

  private var kind: SerialKind = StructureKind.CLASS

  override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeEncoder {
    kind = desc.kind
    bytes += byteArray(0x80 + desc.elementsCount)
    return this
  }

  override fun encodeTaggedString(tag: String, value: String) {
    writeString(tag)
    writeString(value)
  }

  override fun encodeTaggedBoolean(tag: String, value: Boolean) {
    writeString(tag)
    bytes += byteArray(if (value) 0xc3 else 0xc2)
  }

  override fun encodeTaggedByte(tag: String, value: Byte) {
    writeString(tag)
    bytes += ByteArray(1) { value }
  }

  override fun encodeTaggedInt(tag: String, value: Int) {
    if (value <= Byte.MAX_VALUE) return encodeTaggedByte(tag, value.toByte())
    writeString(tag)
    bytes += value.toByteArray()
  }

  override fun encodeTaggedLong(tag: String, value: Long) {
    if (value <= Byte.MAX_VALUE) return encodeTaggedByte(tag, value.toByte())
    if (value <= Int.MAX_VALUE) return encodeTaggedInt(tag, value.toInt())
    writeString(tag)
    bytes += byteArray(0xd3)
    bytes += value.toByteArray()
  }

  override fun encodeTaggedFloat(tag: String, value: Float) {
    writeString(tag)
    bytes += byteArray(0xca) + value.toBits().toByteArray()
  }

  override fun encodeTaggedDouble(tag: String, value: Double) {
    writeString(tag)
    bytes += byteArray(0xcb) + value.toBits().toByteArray()
  }

  override fun encodeTaggedNull(tag: String) {
    writeString(tag)
    bytes += byteArray(0xc0)
  }

  override fun encodeTaggedValue(tag: String, value: Any) {
    if (value is ByteArray) {
      writeString(tag)
      bytes += when {
        value.size <   256 -> byteArray(0xc4) + value.size.toByte()
        value.size < 65536 -> byteArray(0xc5) + value.size.toByteArray().take(2).toByteArray()
        else               -> byteArray(0xc6) + value.size.toByteArray()
      }
      bytes += value
    } else {
      super.encodeTaggedValue(tag, value)
    }
  }

  private fun writeString(str: String) {
    val utf8Bytes = str.toUtf8Bytes()
    bytes += when {
      utf8Bytes.size <    32 -> ByteArray(1) { (0xa0 + utf8Bytes.size).toByte() }
      utf8Bytes.size <   256 -> byteArray(0xd9) + utf8Bytes.size.toByte()
      utf8Bytes.size < 65536 -> byteArray(0xda) + utf8Bytes.size.toByteArray().take(2).toByteArray()
      else                   -> byteArray(0xdb) + utf8Bytes.size.toByteArray()
    }
    bytes += utf8Bytes
  }
}