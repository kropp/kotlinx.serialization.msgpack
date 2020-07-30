import kotlinx.serialization.*
import kotlin.test.*

class ArrayFormatTest {
  @Serializable class A(val v: Array<Int>)

  @Test
  fun empty() = assertArrayEncodeDecode(
      emptyArray(),
      bytes(0x90)
  )

  @Test
  fun ints() = assertArrayEncodeDecode(
      arrayOf(1, 2),
      bytes(0x92, 1, 2)
  )

  @Test
  fun `16 items`() = assertArrayEncodeDecode(
      arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16),
      bytes(0xdc, 0x00, 0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10)
  )

  private fun assertArrayEncodeDecode(arr: Array<Int>, bytes: ByteArray) {
    val encoded = bytes(0x81, 0xa1, 0x76) + bytes
    assertByteArrayEquals(encoded, MessagePack.pack(A.serializer(), A(arr)))
    assertTrue(arr.contentEquals(MessagePack.parse(A.serializer(), encoded).v))
  }
}