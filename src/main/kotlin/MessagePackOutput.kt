import kotlinx.serialization.*

class MessagePackOutput(initial: ByteArray = ByteArray(0)) : NamedValueOutput() {
  internal var bytes: ByteArray = initial
    private set

  override fun writeBegin(desc: KSerialClassDesc, vararg typeParams: KSerializer<*>): KOutput {
    bytes += byteArray(0x80 + desc.associatedFieldsCount)
    return this
  }

  override fun writeTaggedString(tag: String, value: String) {
    writeString(tag)
    writeString(value)
  }

  override fun writeTaggedBoolean(tag: String, value: Boolean) {
    writeString(tag)
    bytes += byteArray(if (value) 0xc3 else 0xc2)
  }

  override fun writeTaggedByte(tag: String, value: Byte) {
    writeString(tag)
    bytes += ByteArray(1) { value }
  }

  override fun writeTaggedInt(tag: String, value: Int) {
    writeString(tag)
    bytes += ByteArray(1) { value.toByteArray()[3] }
  }

  override fun writeTaggedLong(tag: String, value: Long) {
    writeString(tag)
    bytes += ByteArray(1) { value.toByteArray()[7] }
  }

  override fun writeTaggedFloat(tag: String, value: Float) {
    writeString(tag)
    bytes += byteArray(0xca) + value.toBits().toByteArray()
  }

  override fun writeTaggedDouble(tag: String, value: Double) {
    writeString(tag)
    bytes += byteArray(0xcb) + value.toBits().toByteArray()
  }

  override fun writeTaggedNull(tag: String) {
    writeString(tag)
    bytes += byteArray(0xc0)
  }

  override fun writeTaggedValue(tag: String, value: Any) {
    if (value is ByteArray) {
      writeString(tag)
      bytes += when {
        value.size <   256 -> byteArray(0xc4) + value.size.toByte()
        value.size < 65536 -> byteArray(0xc5) + value.size.toByteArray().take(2).toByteArray()
        else               -> byteArray(0xc6) + value.size.toByteArray()
      }
      bytes += value
    } else {
      super.writeTaggedValue(tag, value)
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