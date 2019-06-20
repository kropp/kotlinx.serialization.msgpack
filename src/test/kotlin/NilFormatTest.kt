import kotlinx.serialization.*
import org.junit.*

@ImplicitReflectionSerializer
class NilFormatTest {
  private val bytes = byteArray(0x81, 0xa3, 0x6e, 0x69, 0x6c, 0xc0)

  @Serializable
  class Nil(val nil: String?)

  @Test
  fun testNilRead() {
    val hw = MessagePack.parse<Nil>(bytes)
    Assert.assertEquals(hw.nil, null)
  }

  @Test
  fun testNilWrite() {
    val packed = MessagePack.pack(Nil(nil = null))
    Assert.assertThat(packed, IsByteArrayEqual(bytes))
  }
}