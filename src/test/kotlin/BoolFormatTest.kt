import com.github.kropp.messagepack.*
import kotlinx.serialization.*
import kotlin.test.*

class BoolFormatTest {
  private val bytes = bytes(0x82, 0xa4, 0x74, 0x72, 0x75, 0x65, 0xc3, 0xa5, 0x66, 0x61, 0x6c, 0x73, 0x65, 0xc2)

  @Serializable
  class Bool(val `true`: Boolean, val `false`: Boolean)

  @Test
  fun testRead() {
    val bool = MessagePack.decode(Bool.serializer(), bytes)
    assertEquals(true, bool.`true`)
    assertEquals(false, bool.`false`)
  }

  @Test
  fun testWrite() {
    val packed = MessagePack.encode(Bool.serializer(), Bool(`true` = true, `false` = false))
    assertByteArrayEquals(packed, bytes)
  }
}