import kotlinx.serialization.*
import org.junit.*

private const val epsilon = 0.0000001

class FloatFormatTest {
  private val plusBytes = byteArray(0x82, 0xa5, 0x66, 0x6c, 0x6f, 0x61, 0x74, 0xca, 0x3f, 0x00, 0x00, 0x00, 0xa6, 0x64, 0x6f, 0x75, 0x62, 0x6c, 0x65, 0xcb, 0x3f, 0xe0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
  private val minusBytes = byteArray(0x82, 0xa5, 0x66, 0x6c, 0x6f, 0x61, 0x74, 0xca, 0xbf, 0x00, 0x00, 0x00, 0xa6, 0x64, 0x6f, 0x75, 0x62, 0x6c, 0x65, 0xcb, 0xbf, 0xe0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)

  @Serializable
  class FD(val float: Float, val double: Double)

  @Test
  fun testRead() {
    val p = MessagePack.parse(FD.serializer(), plusBytes)
    val m = MessagePack.parse(FD.serializer(), minusBytes)

    Assert.assertEquals(0.5f, p.float)
    Assert.assertEquals(0.5, p.double, epsilon)
    Assert.assertEquals(-0.5f, m.float)
    Assert.assertEquals(-0.5, m.double, epsilon)
  }

  @Test
  fun testWrite() {
    Assert.assertThat(MessagePack.pack(FD.serializer(), FD(0.5f, 0.5)), IsByteArrayEqual(plusBytes))
    Assert.assertThat(MessagePack.pack(FD.serializer(), FD(-0.5f, -0.5)), IsByteArrayEqual(minusBytes))
  }
}