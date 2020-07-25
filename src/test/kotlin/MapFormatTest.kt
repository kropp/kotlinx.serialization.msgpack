import kotlinx.serialization.*
import org.junit.*
import org.junit.Assert.*

@ImplicitReflectionSerializer
class MapFormatTest {
  @Serializable class M(val v: Map<String, Boolean>)

  @Test
  fun empty() {
    val bytes = byteArray(0x81, 0xa1, 0x76, 0x80)

    assertThat(MessagePack.pack(M(emptyMap())), IsByteArrayEqual(bytes))

    assertEquals(0, MessagePack.parse<M>(bytes).v.size)
  }

  @Test
  fun items() {
    val bytes = byteArray(0x81, 0xa1, 0x76, 0x82, 0xa1, 0x61, 0xc3, 0xa1, 0x62, 0xc2)

    assertThat(MessagePack.pack(M(mapOf("a" to true, "b" to false))), IsByteArrayEqual(bytes))

    val map = MessagePack.parse<M>(bytes).v
    assertEquals(2, map.size)
    assertTrue("a" in map)
    assertEquals(true, map["a"])
    assertTrue("b" in map)
    assertEquals(false, map["b"])
  }
}