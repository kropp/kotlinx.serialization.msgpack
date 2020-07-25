import kotlinx.serialization.*
import java.io.*

class MessagePack {
  fun <T> parse(serial: DeserializationStrategy<T>, bytes: ByteArray): T {
    return serial.deserialize(MessagePackInput(ByteArrayInputStream(bytes)))
  }

  fun <T> pack(serial: SerializationStrategy<T>, obj: T): ByteArray {
    val output = MessagePackOutput()
    serial.serialize(output, obj)
    return output.bytes
  }

  companion object {
    fun <T> parse(serial: DeserializationStrategy<T>, bytes: ByteArray) = MessagePack().parse(serial, bytes)

    @ImplicitReflectionSerializer
    inline fun <reified T : Any> parse(bytes: ByteArray) = MessagePack().parse(T::class.serializer(), bytes)

    fun <T> pack(serial: SerializationStrategy<T>, obj: T): ByteArray = MessagePack().pack(serial, obj)

    @ImplicitReflectionSerializer
    inline fun <reified T : Any> pack(obj: T): ByteArray = MessagePack().pack(T::class.serializer(), obj)
  }
}