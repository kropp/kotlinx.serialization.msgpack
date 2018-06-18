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

  override fun writeTaggedInt(tag: String, value: Int) {
    writeString(tag)
    bytes += byteArray(0x00)
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