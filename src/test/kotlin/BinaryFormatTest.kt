import kotlinx.serialization.Serializable
import org.junit.*
import org.junit.Assert.*

class BinaryFormatTest {
  @Serializable class Bin(val v: ByteArray)


  @Test
  fun empty() {
    assertThat(MessagePack.pack(Bin(ByteArray(0))), IsByteArrayEqual(byteArray(0x81, 0xa1, 0x76, 0xc4, 0x00)))

    assertThat(MessagePack.parse<Bin>(byteArray(0x81, 0xa1, 0x76, 0xc4, 0x00)).v, IsByteArrayEqual(ByteArray(0)))
    assertThat(MessagePack.parse<Bin>(byteArray(0x81, 0xa1, 0x76, 0xc5, 0x00, 0x00)).v, IsByteArrayEqual(ByteArray(0)))
    assertThat(MessagePack.parse<Bin>(byteArray(0x81, 0xa1, 0x76, 0xc6, 0x00, 0x00, 0x00, 0x00)).v, IsByteArrayEqual(ByteArray(0)))
  }

  @Test
  fun nonEmpty() {
    val payload = byteArray(0x00, 0xff)
    assertThat(MessagePack.pack(Bin(payload)), IsByteArrayEqual(byteArray(0x81, 0xa1, 0x76, 0xc4, 0x02) + payload))

    assertThat(MessagePack.parse<Bin>(byteArray(0x81, 0xa1, 0x76, 0xc4, 0x02) + payload).v, IsByteArrayEqual(payload))
    assertThat(MessagePack.parse<Bin>(byteArray(0x81, 0xa1, 0x76, 0xc5, 0x00, 0x02) + payload).v, IsByteArrayEqual(payload))
    assertThat(MessagePack.parse<Bin>(byteArray(0x81, 0xa1, 0x76, 0xc6, 0x00, 0x00, 0x00, 0x02) + payload).v, IsByteArrayEqual(payload))
  }
}