import org.hamcrest.*

class IsByteArrayEqual(private val bytes: ByteArray) : BaseMatcher<ByteArray>() {
  override fun describeTo(description: Description) {
    description.appendText("    [${bytes.hexString()}]")
  }

  override fun describeMismatch(item: Any?, description: Description) {
    if (item is ByteArray) {
      description.appendText("was [${item.hexString()}]")
    } else {
      super.describeMismatch(item, description)
    }
  }

  override fun matches(item: Any?): Boolean {
    val other = item as? ByteArray ?: return false
    return bytes.contentEquals(other)
  }

  private fun ByteArray.hexString() = joinToString(", ") {
    val char = it.toChar()
    if (char in 'a'..'z' || char in 'A'..'Z' || char in '0'..'9') {
      "$char"
    } else {
      "0x${Integer.toHexString((it.toInt() and 0xFF))}"
    }
  }
}