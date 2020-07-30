import kotlinx.serialization.*
import java.io.*

class MessagePack private constructor() {
  companion object {
    fun <T> decode(serial: DeserializationStrategy<T>, bytes: ByteArray): T =
        serial.deserialize(MessagePackDecoder(ByteArrayInputStream(bytes)))

    fun <T> decode(serial: DeserializationStrategy<T>, stream: InputStream): T =
        serial.deserialize(MessagePackDecoder(stream))

    fun <T> encode(serial: SerializationStrategy<T>, obj: T): ByteArray {
      val stream = ByteArrayOutputStream()
      serial.serialize(MessagePackEncoder(stream), obj)
      return stream.toByteArray()
    }
  }
}