# Kotlin Serialization MessagePack library

This library extends [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/) framework to support
[MessagePack](https://msgpack.org/) format.

## Implementation details

When multiple equivalent encodings exist, the shortest (in terms of number of bytes) is used.
Decoders decode any format and convert it to a required type (possibly loosing precision or significant higher digits)

Thus, not for any input decoding and encoding back will produce the exact same byte array. However, for any `@Serializable` class
encoding and decoding should result in the equivalent instance.

## Current Limitations

* Only JVM is supported as of now
* Unsigned integers can't be encoded because kotlinx.serialization framework [doesn't support `inline` classes](https://github.com/Kotlin/kotlinx.serialization/issues/259) (yet)
* MessagePack extensions are not supported

## Verification

The implementation is tested against https://github.com/kawanet/msgpack-test-suite/

## Usage

```kotlin
import com.github.kropp.messagepack.MessagePack

@Serializable
data class Demo(val compact: Boolean, val schema: Int)

val bytes = byteArrayOf(0x82.toByte(), 0xA7.toByte(), 0x63, 0x6F, 0x6D, 0x70, 0x61, 0x63, 0x74, 0xC3.toByte(), 0xA6.toByte(), 0x73, 0x63, 0x68, 0x65, 0x6D, 0x61, 0x00)
val demo = MessagePack.decode(Demo.serializer(), bytes)

assertEquals(demo.compact, true)
assertEquals(demo.schema, 0)
```