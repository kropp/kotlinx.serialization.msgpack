import kotlinx.serialization.*
import org.hamcrest.*
import org.junit.*
import org.junit.Assert.*

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
}

class IsByteArrayEqual(private val bytes: ByteArray) : BaseMatcher<ByteArray>() {
  override fun describeTo(description: Description) {
    description.appendText("    " + bytes.contentToString())
  }

  override fun describeMismatch(item: Any?, description: Description) {
    if (item is ByteArray) {
      description.appendText("was " + item.contentToString())
    } else {
      super.describeMismatch(item, description)
    }
  }

  override fun matches(item: Any?): Boolean {
    val other = item as? ByteArray ?: return false
    return bytes.contentEquals(other)
  }
}
