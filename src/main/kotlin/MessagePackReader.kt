import kotlinx.serialization.*
import java.io.*

class MessagePack {
  fun <T> parse(bytes: ByteArray, serial: DeserializationStrategy<T>): T {
    return serial.deserialize(MessagePackInput(ByteArrayInputStream(bytes)))
  }

  fun <T> pack(obj: T, serial: SerializationStrategy<T>): ByteArray {
    val output = MessagePackOutput()
    serial.serialize(output, obj)
    return output.bytes
  }

  @ImplicitReflectionSerializer
  companion object {
    fun <T> parse(bytes: ByteArray, serial: DeserializationStrategy<T>) = MessagePack().parse(bytes, serial)
    inline fun <reified T : Any> parse(bytes: ByteArray) = MessagePack().parse(bytes, T::class.serializer())

    fun <T> pack(obj: T, serial: SerializationStrategy<T>): ByteArray = MessagePack().pack(obj, serial)
    inline fun <reified T : Any> pack(obj: T): ByteArray = MessagePack().pack(obj, T::class.serializer())
  }
}