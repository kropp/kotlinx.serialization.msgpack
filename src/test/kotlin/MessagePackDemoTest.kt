import kotlinx.serialization.*
import org.junit.*
import org.junit.Assert.*

@ImplicitReflectionSerializer
class MessagePackDemoTest {
  @Serializable
  data class Demo(val compact: Boolean, val schema: Int)

  private val demoBytes = byteArray(0x82, 0xa7, "compact", 0xc3, 0xa6, "schema", 0x00)

  @Test
  fun testMsgPackDeserialization() {
    val demo = MessagePack.parse<Demo>(demoBytes)
    assertEquals(demo.compact, true)
    assertEquals(demo.schema, 0)
  }

  @Test
  fun testMsgPackSerialization() {
    val packed = MessagePack.pack(Demo(true, 0))
    assertThat(packed, IsByteArrayEqual(demoBytes))
  }

  private val helloWorldBytes = byteArray(0x81, 0xa5, 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0xa5, 0x77, 0x6f, 0x72, 0x6c, 0x64)

  @Serializable
  class HW(val hello: String)

  @Test
  fun testHelloWorldRead() {
    val hw = MessagePack.parse<HW>(helloWorldBytes)
    assertEquals(hw.hello, "world")
  }

  @Test
  fun testHelloWorldWrite() {
    val packed = MessagePack.pack(HW(hello = "world"))
    assertThat(packed, IsByteArrayEqual(helloWorldBytes))
  }
}

