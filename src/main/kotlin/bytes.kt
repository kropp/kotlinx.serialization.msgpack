package com.github.kropp.messagepack

import java.io.*

// from kotlinx.serialization.internal
internal fun InputStream.readExactNBytes(bytes: Int): ByteArray {
  val array = ByteArray(bytes)
  var read = 0
  while (read < bytes) {
    val i = this.read(array, read, bytes - read)
    if (i == -1) throw IOException("Unexpected EOF")
    read += i
  }
  return array
}

fun bytes(vararg data: Any) = data.map { d ->
  when(d) {
    is Byte -> ByteArray(1) { d }
    is Int -> ByteArray(1) { d.toByte() }
    is String -> d.toByteArray(Charsets.UTF_8)
    else -> ByteArray(0)
  }
}.fold(ByteArray(0)) { acc, it -> acc + it }

fun Short.toByteArray() = ByteArray(2) { ((this.toInt() shr (1-it) * 8) and 0xFF).toByte() }
fun Int.toByteArray() = ByteArray(4) { ((this shr (3-it) * 8) and 0xFF).toByte() }
fun Long.toByteArray() = ByteArray(8) { ((this shr (7-it) * 8) and 0xFF).toByte() }

fun ByteArray.toShort() =
    (get(0).toInt() and 0xFF shl 8) +
    (get(1).toInt() and 0xFF)

fun ByteArray.toInt() =
    (get(0).toInt() and 0xFF shl 24) +
    (get(1).toInt() and 0xFF shl 16) +
    (get(2).toInt() and 0xFF shl 8) +
    (get(3).toInt() and 0xFF)

fun ByteArray.toLong() =
    (get(0).toLong() and 0xFF shl 56) +
    (get(1).toLong() and 0xFF shl 48) +
    (get(2).toLong() and 0xFF shl 40) +
    (get(3).toLong() and 0xFF shl 32) +
    (get(4).toLong() and 0xFF shl 24) +
    (get(5).toLong() and 0xFF shl 16) +
    (get(6).toLong() and 0xFF shl 8) +
    (get(7).toLong() and 0xFF)
