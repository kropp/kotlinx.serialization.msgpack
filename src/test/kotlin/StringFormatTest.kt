import com.github.kropp.messagepack.*
import kotlinx.serialization.builtins.*
import kotlin.test.*

class StringFormatTest {
  @Test
  fun shortString() {
    val bytes = bytes(0xa1, 0x61)

    assertByteArrayEquals(bytes, MessagePack.encode(String.serializer(), "a"))

    assertEquals("a", MessagePack.decode(String.serializer(), bytes))
    assertEquals("a", MessagePack.decode(String.serializer(), bytes(0xd9, 0x01, 0x61)))
    assertEquals("a", MessagePack.decode(String.serializer(), bytes(0xda, 0x00, 0x01, 0x61)))
    assertEquals("a", MessagePack.decode(String.serializer(), bytes(0xdb, 0x00, 0x00, 0x00, 0x01, 0x61)))
  }

  @Test
  fun longString() {
    val bytes = bytes(0xd9, 0x20, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32)

    val str = "12345678901234567890123456789012"
    assertByteArrayEquals(bytes, MessagePack.encode(String.serializer(), str))

    assertEquals(str, MessagePack.decode(String.serializer(), bytes))
    assertEquals(str, MessagePack.decode(String.serializer(), bytes(0xda, 0x00, 0x20, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32)))
  }

  @Test
  fun utf8() {
    val cyrillic = "Кириллица"
    val cyrillicBytes = bytes(0xb2, 0xd0, 0x9a, 0xd0, 0xb8, 0xd1, 0x80, 0xd0, 0xb8, 0xd0, 0xbb, 0xd0, 0xbb, 0xd0, 0xb8, 0xd1, 0x86, 0xd0, 0xb0)
    val hiragana = "ひらがな"
    val hiraganaBytes = bytes(0xac, 0xe3, 0x81, 0xb2, 0xe3, 0x82, 0x89, 0xe3, 0x81, 0x8c, 0xe3, 0x81, 0xaa)

    assertByteArrayEquals(cyrillicBytes, MessagePack.encode(String.serializer(), cyrillic))
    assertByteArrayEquals(hiraganaBytes, MessagePack.encode(String.serializer(), hiragana))

    assertEquals(cyrillic, MessagePack.decode(String.serializer(), cyrillicBytes))
    assertEquals(hiragana, MessagePack.decode(String.serializer(), hiraganaBytes))
  }

  @Test
  fun emoji() {
    val bytes = bytes(0xa4, 0xf0, 0x9f, 0x8d, 0xba)

    assertByteArrayEquals(bytes, MessagePack.encode(String.serializer(), "\uD83C\uDF7A"))

    assertEquals("\uD83C\uDF7A", MessagePack.decode(String.serializer(), bytes))
  }
}