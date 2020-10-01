import kotlinx.serialization.builtins.*
import kotlin.test.*

class IntFormatTest {
  @Test
  fun zero() {
    val bytes = bytes(0x00)

    assertByteArrayEquals(bytes, MessagePack.encode( Byte.serializer(), 0))
    assertByteArrayEquals(bytes, MessagePack.encode(Short.serializer(), 0))
    assertByteArrayEquals(bytes, MessagePack.encode(  Int.serializer(), 0))
    assertByteArrayEquals(bytes, MessagePack.encode( Long.serializer(), 0))

    assertEquals(0.toByte() , MessagePack.decode( Byte.serializer(), bytes))
    assertEquals(0.toShort(), MessagePack.decode(Short.serializer(), bytes))
    assertEquals(0          , MessagePack.decode(  Int.serializer(), bytes))
    assertEquals(0.toLong() , MessagePack.decode( Long.serializer(), bytes))
  }

  @Test
  fun one() {
    val bytes = bytes(0x01)

    assertByteArrayEquals(bytes, MessagePack.encode( Byte.serializer(), 1))
    assertByteArrayEquals(bytes, MessagePack.encode(Short.serializer(), 1))
    assertByteArrayEquals(bytes, MessagePack.encode(  Int.serializer(), 1))
    assertByteArrayEquals(bytes, MessagePack.encode( Long.serializer(), 1))

    assertEquals(1, MessagePack.decode(Int.serializer(), bytes))
    assertEquals(1, MessagePack.decode(Int.serializer(), bytes(0xcc, 0x01)))
    assertEquals(1, MessagePack.decode(Int.serializer(), bytes(0xcd, 0x00, 0x01)))
    assertEquals(1, MessagePack.decode(Int.serializer(), bytes(0xce, 0x00, 0x00, 0x00, 0x01)))
    assertEquals(1L, MessagePack.decode(Long.serializer(), bytes(0xcf, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)))

    assertEquals(1, MessagePack.decode(Int.serializer(), bytes(0xd0, 0x01)))
    assertEquals(1, MessagePack.decode(Int.serializer(), bytes(0xd1, 0x00, 0x01)))
    assertEquals(1, MessagePack.decode(Int.serializer(), bytes(0xd2, 0x00, 0x00, 0x00, 0x01)))
    assertEquals(1L, MessagePack.decode(Long.serializer(), bytes(0xd3, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)))
  }

  @Test
  fun test128() {
    val bytes = bytes(0xd1, 0x00, 0x80)

    assertByteArrayEquals(bytes, MessagePack.encode(Short.serializer(), 128))
    assertByteArrayEquals(bytes, MessagePack.encode(  Int.serializer(), 128))
    assertByteArrayEquals(bytes, MessagePack.encode( Long.serializer(), 128))

    assertEquals(128.toShort(), MessagePack.decode(Short.serializer(), bytes))
    assertEquals(128          , MessagePack.decode(  Int.serializer(), bytes))
    assertEquals(128.toLong() , MessagePack.decode( Long.serializer(), bytes))
  }

  @Test
  fun test255() {
    val bytes = bytes(0xd1, 0x00, 0xff)

    assertByteArrayEquals(bytes, MessagePack.encode(Short.serializer(), 255))
    assertByteArrayEquals(bytes, MessagePack.encode(  Int.serializer(), 255))
    assertByteArrayEquals(bytes, MessagePack.encode( Long.serializer(), 255))

    assertEquals(255.toShort(), MessagePack.decode(Short.serializer(), bytes))
    assertEquals(255          , MessagePack.decode(  Int.serializer(), bytes))
    assertEquals(255.toLong() , MessagePack.decode( Long.serializer(), bytes))
  }

  @Test
  fun test256() {
    val bytes = bytes(0xd1, 0x01, 0x00)

    assertByteArrayEquals(bytes, MessagePack.encode(Short.serializer(), 256))
    assertByteArrayEquals(bytes, MessagePack.encode(  Int.serializer(), 256))
    assertByteArrayEquals(bytes, MessagePack.encode( Long.serializer(), 256))

    assertEquals(256.toShort(), MessagePack.decode(Short.serializer(), bytes))
    assertEquals(256          , MessagePack.decode(  Int.serializer(), bytes))
    assertEquals(256.toLong() , MessagePack.decode( Long.serializer(), bytes))
  }

/*
  @Test
  fun test65535() {
    val bytes = bytes(0xd1, 0xff, 0xff)

    assertByteArrayEquals(bytes, MessagePack.encode(Short.serializer(), 65535))
    assertByteArrayEquals(bytes, MessagePack.encode(  Int.serializer(), 65535))
    assertByteArrayEquals(bytes, MessagePack.encode( Long.serializer(), 65535))

    assertEquals(65535.toShort(), MessagePack.decode(Short.serializer(), bytes))
    assertEquals(65535          , MessagePack.decode(  Int.serializer(), bytes))
    assertEquals(65535.toLong() , MessagePack.decode( Long.serializer(), bytes))
  }
*/

  @Test
  fun intMaxValue() {
    val bytes = bytes(0xd3, 0x00, 0x00, 0x00, 0x00, 0x80, 0x00, 0x00, 0x00)

    assertByteArrayEquals(bytes, MessagePack.encode(Long.serializer(), 2147483648))

    assertEquals(2147483648, MessagePack.decode(Long.serializer(), bytes))
//    assertEquals(2147483648, MessagePack.parse(Long.serializer(), byteArray(0xce, 0x80, 0x00, 0x00, 0x00)))
//    assertEquals(2147483648, MessagePack.parse(Long.serializer(), byteArray(0xca, 0x4f, 0x00, 0x00, 0x00)))
  }
}