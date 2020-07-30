import kotlinx.serialization.*
import kotlin.test.*

class MapFormatTest {
  @Serializable class M(val v: Map<String, Boolean>)

  @Test
  fun empty() {
    val bytes = bytes(0x81, 0xa1, 0x76, 0x80)

    assertByteArrayEquals(bytes, MessagePack.encode(M.serializer(), M(emptyMap())))

    assertEquals(0, MessagePack.decode(M.serializer(), bytes).v.size)
  }

  @Test
  fun items() {
    val bytes = bytes(0x81, 0xa1, 0x76, 0x82, 0xa1, 0x61, 0xc3, 0xa1, 0x62, 0xc2)

    assertByteArrayEquals(bytes, MessagePack.encode(M.serializer(), M(mapOf("a" to true, "b" to false))))

    val map = MessagePack.decode(M.serializer(), bytes).v
    assertEquals(2, map.size)
    assertTrue("a" in map)
    assertEquals(true, map["a"])
    assertTrue("b" in map)
    assertEquals(false, map["b"])
  }
}