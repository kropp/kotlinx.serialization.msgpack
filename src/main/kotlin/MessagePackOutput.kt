import kotlinx.serialization.*
import kotlinx.serialization.builtins.*

class MessagePackOutput(initial: ByteArray = ByteArray(0)) : AbstractEncoder() {
  internal var bytes: ByteArray = initial
    private set

  override fun beginStructure(descriptor: SerialDescriptor, vararg typeSerializers: KSerializer<*>): CompositeEncoder {
    bytes += byteArray(0x80 + descriptor.elementsCount)
    return this
  }

  override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
    if (descriptor.kind != StructureKind.LIST && descriptor.kind != StructureKind.MAP) {
      encodeString(descriptor.getElementName(index))
    }
    return true
  }

  override fun endStructure(descriptor: SerialDescriptor) {}

  override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int, vararg typeSerializers: KSerializer<*>): CompositeEncoder {
    val baseType = if (descriptor.kind == StructureKind.MAP) 0x80 else 0x90
    bytes += byteArray(baseType + collectionSize)
    return this
  }

  override fun encodeString(value: String) {
    val utf8Bytes = value.toUtf8Bytes()
    bytes += when {
      utf8Bytes.size <    32 -> ByteArray(1) { (0xa0 + utf8Bytes.size).toByte() }
      utf8Bytes.size <   256 -> byteArray(0xd9) + utf8Bytes.size.toByte()
      utf8Bytes.size < 65536 -> byteArray(0xda) + utf8Bytes.size.toByteArray().take(2).toByteArray()
      else                   -> byteArray(0xdb) + utf8Bytes.size.toByteArray()
    }
    bytes += utf8Bytes
  }

  override fun encodeBoolean(value: Boolean) {
    bytes += byteArray(if (value) 0xc3 else 0xc2)
  }

  override fun encodeByte(value: Byte) {
    bytes += ByteArray(1) { value }
  }

  override fun encodeInt(value: Int) {
    if (value <= Byte.MAX_VALUE) return encodeByte(value.toByte())
    bytes += value.toByteArray()
  }

  override fun encodeLong(value: Long) {
    if (value <= Byte.MAX_VALUE) return encodeByte(value.toByte())
    if (value <= Int.MAX_VALUE) return encodeInt(value.toInt())
    bytes += byteArray(0xd3)
    bytes += value.toByteArray()
  }

  override fun encodeFloat(value: Float) {
    bytes += byteArray(0xca) + value.toBits().toByteArray()
  }

  override fun encodeDouble(value: Double) {
    bytes += byteArray(0xcb) + value.toBits().toByteArray()
  }

  override fun encodeNull() {
    bytes += byteArray(0xc0)
  }

  override fun encodeValue(value: Any) {
    if (value is ByteArray) {
      bytes += when {
        value.size <   256 -> byteArray(0xc4) + value.size.toByte()
        value.size < 65536 -> byteArray(0xc5) + value.size.toByteArray().take(2).toByteArray()
        else               -> byteArray(0xc6) + value.size.toByteArray()
      }
      bytes += value
    } else {
      super.encodeValue(value)
    }
  }
}