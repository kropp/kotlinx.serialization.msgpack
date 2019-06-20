import kotlinx.serialization.*
import org.junit.*
import org.junit.Assert.*

@ImplicitReflectionSerializer
class ArrayFormatTest {
  @Serializable class A(val v: Array<Int>)

  @Test
  fun empty() {
    val bytes = byteArray(0x81, 0xa1, 0x76, 0x90)

    assertThat(MessagePack.pack(A(emptyArray())), IsByteArrayEqual(bytes))

    assertArrayEquals(emptyArray(), MessagePack.parse<A>(bytes).v)
  }

  @Test
  fun ints() {
    val bytes = byteArray(0x81, 0xa1, 0x76, 0x92, 1, 2)
    val arr = arrayOf(1, 2)

    assertThat(MessagePack.pack(A(arr)), IsByteArrayEqual(bytes))

    assertArrayEquals(arr, MessagePack.parse<A>(bytes).v)
  }
}