import kotlinx.serialization.*
import kotlin.test.*

class IntFormatTest {
  @Serializable class B(val v: Byte)
  @Serializable class I(val v: Int)
  @Serializable class L(val v: Long)


  @Test
  fun zero() {
    val bytes = bytes(0x81, 0xa1, 0x76, 0x00)

    assertByteArrayEquals(bytes, MessagePack.pack(B.serializer(), B(0)))
    assertByteArrayEquals(bytes, MessagePack.pack(I.serializer(), I(0)))
    assertByteArrayEquals(bytes, MessagePack.pack(L.serializer(), L(0)))

    assertEquals(0.toByte(), MessagePack.parse(B.serializer(), bytes).v)
    assertEquals(0         , MessagePack.parse(I.serializer(), bytes).v)
    assertEquals(0.toLong(), MessagePack.parse(L.serializer(), bytes).v)
  }

  @Test
  fun one() {
    val bytes = bytes(0x81, 0xa1, 0x76, 0x01)

    assertByteArrayEquals(bytes, MessagePack.pack(B.serializer(), B(1)))
    assertByteArrayEquals(bytes, MessagePack.pack(I.serializer(), I(1)))
    assertByteArrayEquals(bytes, MessagePack.pack(L.serializer(), L(1)))

    assertEquals(1, MessagePack.parse(I.serializer(), bytes).v)
    assertEquals(1, MessagePack.parse(I.serializer(), bytes(0x81, 0xa1, 0x76, 0xcc, 0x01)).v)
    assertEquals(1, MessagePack.parse(I.serializer(), bytes(0x81, 0xa1, 0x76, 0xcd, 0x00, 0x01)).v)
    assertEquals(1, MessagePack.parse(I.serializer(), bytes(0x81, 0xa1, 0x76, 0xce, 0x00, 0x00, 0x00, 0x01)).v)
    assertEquals(1L, MessagePack.parse(L.serializer(), bytes(0x81, 0xa1, 0x76, 0xcf, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)).v)

    assertEquals(1, MessagePack.parse(I.serializer(), bytes(0x81, 0xa1, 0x76, 0xd0, 0x01)).v)
    assertEquals(1, MessagePack.parse(I.serializer(), bytes(0x81, 0xa1, 0x76, 0xd1, 0x00, 0x01)).v)
    assertEquals(1, MessagePack.parse(I.serializer(), bytes(0x81, 0xa1, 0x76, 0xd2, 0x00, 0x00, 0x00, 0x01)).v)
    assertEquals(1L, MessagePack.parse(L.serializer(), bytes(0x81, 0xa1, 0x76, 0xd3, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)).v)
  }

  @Test
  fun intMaxValue() {
    val bytes = bytes(0x81, 0xa1, 0x76, 0xd3, 0x00, 0x00, 0x00, 0x00, 0x80, 0x00, 0x00, 0x00)

    assertByteArrayEquals(bytes, MessagePack.pack(L.serializer(), L(2147483648)))

    assertEquals(2147483648, MessagePack.parse(L.serializer(), bytes).v)
//    assertEquals(2147483648, MessagePack.parse(L.serializer(), byteArray(0x81, 0xa1, 0x76, 0xce, 0x80, 0x00, 0x00, 0x00)).v)
//    assertEquals(2147483648, MessagePack.parse(L.serializer(), byteArray(0x81, 0xa1, 0x76, 0xca, 0x4f, 0x00, 0x00, 0x00)).v)
  }
}