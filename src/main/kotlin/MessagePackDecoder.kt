import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.io.*

class MessagePackDecoder private constructor(private val input: InputStream, private val count: Int) : AbstractDecoder() {
  private var remaining: Int = count

  constructor(input: InputStream): this(input, 0)

  override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
    val type = input.read()
    return when {
      type == 0xdc -> MessagePackDecoder(input, input.readExactNBytes(2).toShort())
      type == 0xdd -> MessagePackDecoder(input, input.readExactNBytes(4).toInt())
      type and 0x90 == 0x90 -> MessagePackDecoder(input, type - 0x90)
      type and 0x80 == 0x80 -> {
        var c = type - 0x80
        if (descriptor.kind == StructureKind.MAP) {
          // map like structures stored as key-value pairs,
          // so each entry is decoded as two consecutive elements
          c *= 2
        }
        MessagePackDecoder(input, c)
      }
      else -> throw SerializationException("Unexpected structure type: $type")
    }
  }

  override fun decodeValue(): Any {
    return readNext()!!
  }

  override fun decodeByte(): Byte {
    return when (val v = decodeValue()) {
      is Byte -> v
      is Int -> v.toByte()
      is Long -> v.toByte()
      else -> v as Byte
    }
  }

  override fun decodeLong(): Long {
    return when (val v = decodeValue()) {
      is Byte -> v.toLong()
      is Int -> v.toLong()
      is Long -> v
      else -> v as Long
    }
  }

  override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
    return count
  }

  override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
    if (remaining <= 0) return CompositeDecoder.DECODE_DONE
    remaining--
    if (descriptor.kind == StructureKind.LIST || descriptor.kind == StructureKind.MAP) {
      return count - remaining - 1
    }
    val name = readNext() as String
    return descriptor.getElementIndex(name)
  }

  override fun decodeNotNullMark(): Boolean {
    input.mark(1)
    val peek = input.read()
    input.reset()
    return peek != 0xc0
  }

  private fun readNext(): Any? {
    val type = input.read()
    when (type) {
      0x00 -> return 0
      0xc0 -> return null
      0xc2 -> return false
      0xc3 -> return true
      0xc4 -> {
        val length = input.readExactNBytes(1)[0].toInt()
        return input.readExactNBytes(length)
      }
      0xc5 -> {
        val length = input.readExactNBytes(2).toShort()
        return input.readExactNBytes(length)
      }
      0xc6 -> {
        val length = input.readExactNBytes(4).toInt()
        return input.readExactNBytes(length)
      }
      0xca -> return Float.fromBits(input.readExactNBytes(4).toInt())
      0xcb -> return Double.fromBits(input.readExactNBytes(8).toLong())
      0xcc -> return input.readExactNBytes(1)[0].toInt()
      0xcd -> return input.readExactNBytes(2).toShort()
      0xce -> return input.readExactNBytes(4).toInt()
      0xcf -> return input.readExactNBytes(8).toLong()
      0xd0 -> return input.readExactNBytes(1)[0].toInt()
      0xd1 -> return input.readExactNBytes(2).toShort()
      0xd2 -> return input.readExactNBytes(4).toInt()
      0xd3 -> return input.readExactNBytes(8).toLong()
      0xd9 -> {
        val length = input.readExactNBytes(1)[0].toInt()
        return String(input.readExactNBytes(length))
      }
      0xda -> {
        val length = input.readExactNBytes(2).toShort()
        return String(input.readExactNBytes(length))
      }
      0xdb -> {
        val length = input.readExactNBytes(4).toInt()
        return String(input.readExactNBytes(length))
      }
    }
    return when {
      type or 0b01111111 == 0b01111111 -> type
      type and 0b11100000 == 0b11100000 -> type
      type and 0xa0 == 0xa0 -> {
        val length = type - 0xa0
        String(input.readExactNBytes(length))
      }
      type and 0x90 == 0x90 -> {
        val size = type - 0x90
        Array(size) { readNext() }
      }
      else -> throw IllegalStateException("Unexpected byte ${type.toString(16)}")
    }
  }
}