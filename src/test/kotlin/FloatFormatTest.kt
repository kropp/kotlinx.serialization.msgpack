import com.github.kropp.messagepack.*
import kotlinx.serialization.*
import kotlin.test.*

private const val epsilon = 0.0000001

class FloatFormatTest {
  private val plusBytes = bytes(0x82, 0xa5, 0x66, 0x6c, 0x6f, 0x61, 0x74, 0xca, 0x3f, 0x00, 0x00, 0x00, 0xa6, 0x64, 0x6f, 0x75, 0x62, 0x6c, 0x65, 0xcb, 0x3f, 0xe0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
  private val minusBytes = bytes(0x82, 0xa5, 0x66, 0x6c, 0x6f, 0x61, 0x74, 0xca, 0xbf, 0x00, 0x00, 0x00, 0xa6, 0x64, 0x6f, 0x75, 0x62, 0x6c, 0x65, 0xcb, 0xbf, 0xe0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)

  @Serializable
  class FD(val float: Float, val double: Double)

  @Test
  fun testRead() {
    val p = MessagePack.decode(FD.serializer(), plusBytes)
    val m = MessagePack.decode(FD.serializer(), minusBytes)

    assertEquals(0.5f, p.float)
    assertDoubleEquals(0.5, p.double, epsilon)
    assertEquals(-0.5f, m.float)
    assertDoubleEquals(-0.5, m.double, epsilon)
  }

  @Test
  fun testWrite() {
    assertByteArrayEquals(plusBytes, MessagePack.encode(FD.serializer(), FD(0.5f, 0.5)))
    assertByteArrayEquals(minusBytes, MessagePack.encode(FD.serializer(), FD(-0.5f, -0.5)))
  }
}