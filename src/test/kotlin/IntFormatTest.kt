import kotlinx.serialization.Serializable
import org.junit.*
import org.junit.Assert.*

class IntFormatTest {
  @Serializable class B(val v: Byte)
  @Serializable class I(val v: Int)
  @Serializable class L(val v: Long)


  @Test
  fun zero() {
    val bytes = byteArray(0x81, 0xa1, 0x76, 0x00)

    assertThat(MessagePack.pack(B(0)), IsByteArrayEqual(bytes))
    assertThat(MessagePack.pack(I(0)), IsByteArrayEqual(bytes))
    assertThat(MessagePack.pack(L(0)), IsByteArrayEqual(bytes))

    assertEquals(0.toByte(), MessagePack.parse<B>(bytes).v)
    assertEquals(0         , MessagePack.parse<I>(bytes).v)
    assertEquals(0.toLong(), MessagePack.parse<L>(bytes).v)
  }

  @Test
  fun one() {
    val bytes = byteArray(0x81, 0xa1, 0x76, 0x01)

    assertThat(MessagePack.pack(B(1)), IsByteArrayEqual(bytes))
    assertThat(MessagePack.pack(I(1)), IsByteArrayEqual(bytes))
    assertThat(MessagePack.pack(L(1)), IsByteArrayEqual(bytes))

    assertEquals(1, MessagePack.parse<I>(bytes).v)
    assertEquals(1, MessagePack.parse<I>(byteArray(0x81, 0xa1, 0x76, 0xcc, 0x01)).v)
    assertEquals(1, MessagePack.parse<I>(byteArray(0x81, 0xa1, 0x76, 0xcd, 0x00, 0x01)).v)
    assertEquals(1, MessagePack.parse<I>(byteArray(0x81, 0xa1, 0x76, 0xce, 0x00, 0x00, 0x00, 0x01)).v)
    assertEquals(1L, MessagePack.parse<L>(byteArray(0x81, 0xa1, 0x76, 0xcf, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)).v)

    assertEquals(1, MessagePack.parse<I>(byteArray(0x81, 0xa1, 0x76, 0xd0, 0x01)).v)
    assertEquals(1, MessagePack.parse<I>(byteArray(0x81, 0xa1, 0x76, 0xd1, 0x00, 0x01)).v)
    assertEquals(1, MessagePack.parse<I>(byteArray(0x81, 0xa1, 0x76, 0xd2, 0x00, 0x00, 0x00, 0x01)).v)
    assertEquals(1L, MessagePack.parse<L>(byteArray(0x81, 0xa1, 0x76, 0xd3, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)).v)
  }
}