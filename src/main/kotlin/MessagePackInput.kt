import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import java.io.*

class MessagePackInput(private val input: InputStream) : AbstractDecoder() {
  private val reader = MessagePackBinaryReader(input)
  private var count: Int = 0
  private var left: Int = 0
  private var kind: StructureKind = StructureKind.CLASS

  override fun beginStructure(descriptor: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
    val type = input.read()
    when {
      type and 0x90 == 0x90 -> {
        kind = StructureKind.LIST
        count = type - 0x90
      }
      type and 0x80 == 0x80 -> count = type - 0x80
    }
    if (descriptor.kind == StructureKind.MAP) {
      // map like structures stored as key-value pairs,
      // so each entry is decoded as two consecutive elements
      count *= 2
    }
    left = count
    return this
  }

  override fun decodeValue(): Any {
    return reader.readNext()!!
  }

  override fun decodeByte(): Byte {
    val v = decodeValue()
    return when (v) {
      is Byte -> v
      is Int -> v.toByte()
      is Long -> v.toByte()
      else -> v as Byte
    }
  }

  override fun decodeLong(): Long {
    val v = decodeValue()
    return when (v) {
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
    if (left <= 0) return CompositeDecoder.READ_DONE
    left--
    if (descriptor.kind == StructureKind.LIST || descriptor.kind == StructureKind.MAP) {
      return count - left - 1
    }
    val name = reader.readString()
    return descriptor.getElementIndex(name)
  }

  override fun decodeNotNullMark(): Boolean {
    input.mark(1)
    val peek = input.read()
    input.reset()
    return peek != 0xc0
  }
}