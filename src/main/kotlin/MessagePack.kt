import kotlinx.serialization.*
import java.io.*

class MessagePack private constructor() {
  companion object {
    fun <T> parse(serial: DeserializationStrategy<T>, bytes: ByteArray): T {
      return serial.deserialize(MessagePackDecoder(ByteArrayInputStream(bytes)))
    }

    fun <T> pack(serial: SerializationStrategy<T>, obj: T): ByteArray {
      val output = MessagePackEncoder()
      serial.serialize(output, obj)
      return output.bytes
    }
  }
}