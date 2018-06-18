import kotlinx.serialization.*

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