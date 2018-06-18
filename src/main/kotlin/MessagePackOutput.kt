import kotlinx.serialization.*

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