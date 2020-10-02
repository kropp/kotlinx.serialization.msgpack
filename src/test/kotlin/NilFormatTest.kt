import com.github.kropp.messagepack.*
import kotlinx.serialization.*
import kotlin.test.*

class NilFormatTest {
  private val bytes = bytes(0x81, 0xa3, 0x6e, 0x69, 0x6c, 0xc0)

  @Serializable
  class Nil(val nil: String?)

  @Test
  fun testNilRead() {
    val hw = MessagePack.decode(Nil.serializer(), bytes)
    assertEquals(hw.nil, null)
  }

  @Test
  fun testNilWrite() {
    val packed = MessagePack.encode(Nil.serializer(), Nil(nil = null))
    assertByteArrayEquals(bytes, packed)
  }
}