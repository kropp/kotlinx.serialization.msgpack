import kotlinx.serialization.*
import org.junit.*

class BoolFormatTest {
  private val bytes = byteArray(0x82, 0xa4, 0x74, 0x72, 0x75, 0x65, 0xc3, 0xa5, 0x66, 0x61, 0x6c, 0x73, 0x65, 0xc2)

  @Serializable
  class Bool(val `true`: Boolean, val `false`: Boolean)

  @Test
  fun testRead() {
    val bool = MessagePack.parse(Bool.serializer(), bytes)
    Assert.assertEquals(true, bool.`true`)
    Assert.assertEquals(false, bool.`false`)
  }

  @Test
  fun testWrite() {
    val packed = MessagePack.pack(Bool.serializer(), Bool(`true` = true, `false` = false))
    Assert.assertThat(packed, IsByteArrayEqual(bytes))
  }
}