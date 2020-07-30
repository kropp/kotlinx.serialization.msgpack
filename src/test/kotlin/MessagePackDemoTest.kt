import kotlinx.serialization.*
import kotlin.test.*

class MessagePackDemoTest {
  @Serializable
  data class Demo(val compact: Boolean, val schema: Int)

  private val demoBytes = bytes(0x82, 0xa7, "compact", 0xc3, 0xa6, "schema", 0x00)

  @Test
  fun testMsgPackDeserialization() {
    val demo = MessagePack.decode(Demo.serializer(), demoBytes)
    assertEquals(demo.compact, true)
    assertEquals(demo.schema, 0)
  }

  @Test
  fun testMsgPackSerialization() {
    val packed = MessagePack.encode(Demo.serializer(), Demo(true, 0))
    assertByteArrayEquals(demoBytes, packed)
  }

  private val helloWorldBytes = bytes(0x81, 0xa5, 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0xa5, 0x77, 0x6f, 0x72, 0x6c, 0x64)

  @Serializable
  class HW(val hello: String)

  @Test
  fun testHelloWorldRead() {
    val hw = MessagePack.decode(HW.serializer(), helloWorldBytes)
    assertEquals(hw.hello, "world")
  }

  @Test
  fun testHelloWorldWrite() {
    val packed = MessagePack.encode(HW.serializer(), HW(hello = "world"))
    assertByteArrayEquals(helloWorldBytes, packed)
  }

  @Test
  fun nested() {
    @Serializable
    class Nested(val many: Array<Int>, val demo: Demo)

    val bytes = bytes(0x82, 0xa4, "many", 0x90, 0xa4, "demo", 0x82, 0xa7, "compact", 0xc3, 0xa6, "schema", 0x00)

    val nested = MessagePack.decode(Nested.serializer(), bytes)
    assertEquals(0, nested.many.size)
    assertEquals(true, nested.demo.compact)
    assertEquals(0, nested.demo.schema)
    assertByteArrayEquals(bytes, MessagePack.encode(Nested.serializer(), nested))
  }
}

