import kotlin.math.*
import kotlin.test.*

fun assertByteArrayEquals(expected: ByteArray, actual: ByteArray, message: String? = null) =
    asserter.assertTrue({ (if (message == null) "" else "$message. ") + "Expected <$expected>, actual <$actual>." }, expected.contentEquals(actual))

fun assertFloatEquals(expected: Float, actual: Float, precision: Float, message: String? = null) =
    asserter.assertTrue({ (if (message == null) "" else "$message. ") + "Expected <$expected>, actual <$actual>." }, abs(expected - actual) < precision)

fun assertDoubleEquals(expected: Double, actual: Double, precision: Double, message: String? = null) =
    asserter.assertTrue({ (if (message == null) "" else "$message. ") + "Expected <$expected>, actual <$actual>." }, abs(expected - actual) < precision)