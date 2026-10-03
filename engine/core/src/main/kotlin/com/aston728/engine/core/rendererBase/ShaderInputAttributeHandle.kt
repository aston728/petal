package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.math.*
import java.nio.ByteBuffer

private class ShaderInputAttributeDescriptor<T>(
    val primitive: ShaderAttributePrimitive, val type: ShaderAttributeType,
    val updater: (ByteBuffer, Int, T) -> Unit
)
private object ShaderInputAttributeDescriptors {
    val Int = ShaderInputAttributeDescriptor<Int>(ShaderAttributePrimitive.Int, ShaderAttributeType.Int) { buffer, i, value ->
        buffer.putInt(i, value)
    }
    val NormalizedInt = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Int, ShaderAttributeType.Float, this.Int.updater)
    val IVec2 = ShaderInputAttributeDescriptor<IVec2>(ShaderAttributePrimitive.Int, ShaderAttributeType.IVec2) { buffer, i, value ->
        buffer.putInt(i + 0, value.first)
        buffer.putInt(i + 4, value.second)
    }
    val NormalizedIVec2 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Int, ShaderAttributeType.Vec2, this.IVec2.updater)
    val IVec3 = ShaderInputAttributeDescriptor<IVec3>(ShaderAttributePrimitive.Int, ShaderAttributeType.IVec3) { buffer, i, value ->
        buffer.putInt(i + 0, value.first)
        buffer.putInt(i + 4, value.second)
        buffer.putInt(i + 8, value.third)
    }
    val NormalizedIVec3 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Int, ShaderAttributeType.Vec3, this.IVec3.updater)
    val IVec4 = ShaderInputAttributeDescriptor<IVec4>(ShaderAttributePrimitive.Int, ShaderAttributeType.IVec4) { buffer, i, value ->
        buffer.putInt(i + 0 , value.first)
        buffer.putInt(i + 4 , value.second)
        buffer.putInt(i + 8 , value.third)
        buffer.putInt(i + 12, value.fourth)
    }
    val NormalizedIVec4 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Int, ShaderAttributeType.Vec4, this.IVec4.updater)

    val Byte = ShaderInputAttributeDescriptor<Byte>(ShaderAttributePrimitive.Byte, ShaderAttributeType.Int) { buffer, i, value ->
        buffer.put(i, value)
    }
    val NormalizedByte = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Byte, ShaderAttributeType.Float, this.Byte.updater)
    val BVec2 = ShaderInputAttributeDescriptor<BVec2>(ShaderAttributePrimitive.Byte, ShaderAttributeType.IVec2) { buffer, i, value ->
        buffer.put(i + 0, value.first)
        buffer.put(i + 1, value.second)
    }
    val NormalizedBVec2 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Byte, ShaderAttributeType.Vec2, this.BVec2.updater)
    val BVec3 = ShaderInputAttributeDescriptor<BVec3>(ShaderAttributePrimitive.Byte, ShaderAttributeType.IVec3) { buffer, i, value ->
        buffer.put(i + 0, value.first)
        buffer.put(i + 1, value.second)
        buffer.put(i + 2, value.third)
    }
    val NormalizedBVec3 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Byte, ShaderAttributeType.Vec3, this.BVec3.updater)
    val BVec4 = ShaderInputAttributeDescriptor<BVec4>(ShaderAttributePrimitive.Byte, ShaderAttributeType.IVec4) { buffer, i, value ->
        buffer.put(i + 0, value.first)
        buffer.put(i + 1, value.second)
        buffer.put(i + 2, value.third)
        buffer.put(i + 3, value.fourth)
    }
    val NormalizedBVec4 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Byte, ShaderAttributeType.Vec4, this.BVec4.updater)

    val Short = ShaderInputAttributeDescriptor<Short>(ShaderAttributePrimitive.Short, ShaderAttributeType.Int) { buffer, i, value ->
        buffer.putShort(i, value)
    }
    val NormalizedShort = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Short, ShaderAttributeType.Float, this.Short.updater)
    val SVec2 = ShaderInputAttributeDescriptor<SVec2>(ShaderAttributePrimitive.Short, ShaderAttributeType.IVec2) { buffer, i, value ->
        buffer.putShort(i + 0, value.first)
        buffer.putShort(i + 2, value.second)
    }
    val NormalizedSVec2 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Short, ShaderAttributeType.Vec2, this.SVec2.updater)
    val SVec3 = ShaderInputAttributeDescriptor<SVec3>(ShaderAttributePrimitive.Short, ShaderAttributeType.IVec3) { buffer, i, value ->
        buffer.putShort(i + 0, value.first)
        buffer.putShort(i + 2, value.second)
        buffer.putShort(i + 4, value.third)
    }
    val NormalizedSVec3 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Short, ShaderAttributeType.Vec3, this.SVec3.updater)
    val SVec4 = ShaderInputAttributeDescriptor<SVec4>(ShaderAttributePrimitive.Short, ShaderAttributeType.IVec4) { buffer, i, value ->
        buffer.putShort(i + 0, value.first)
        buffer.putShort(i + 2, value.second)
        buffer.putShort(i + 4, value.third)
        buffer.putShort(i + 6, value.fourth)
    }
    val NormalizedSVec4 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.Short, ShaderAttributeType.Vec4, this.SVec4.updater)

    val UInt = ShaderInputAttributeDescriptor<UInt>(ShaderAttributePrimitive.UInt, ShaderAttributeType.UInt) { buffer, i, value ->
        buffer.putInt(i, value.toInt())
    }
    val NormalizedUInt = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UInt, ShaderAttributeType.Float, this.UInt.updater)
    val UIVec2 = ShaderInputAttributeDescriptor<UIVec2>(ShaderAttributePrimitive.UInt, ShaderAttributeType.UIVec2) { buffer, i, value ->
        buffer.putInt(i + 0, value.first.toInt())
        buffer.putInt(i + 4, value.second.toInt())
    }
    val NormalizedUIVec2 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UInt, ShaderAttributeType.Vec2, this.UIVec2.updater)
    val UIVec3 = ShaderInputAttributeDescriptor<UIVec3>(ShaderAttributePrimitive.UInt, ShaderAttributeType.UIVec3) { buffer, i, value ->
        buffer.putInt(i + 0, value.first.toInt())
        buffer.putInt(i + 4, value.second.toInt())
        buffer.putInt(i + 8, value.third.toInt())
    }
    val NormalizedUIVec3 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UInt, ShaderAttributeType.Vec3, this.UIVec3.updater)
    val UIVec4 = ShaderInputAttributeDescriptor<UIVec4>(ShaderAttributePrimitive.UInt, ShaderAttributeType.UIVec4) { buffer, i, value ->
        buffer.putInt(i + 0 , value.first.toInt())
        buffer.putInt(i + 4 , value.second.toInt())
        buffer.putInt(i + 8 , value.third.toInt())
        buffer.putInt(i + 12, value.fourth.toInt())
    }
    val NormalizedUIVec4 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UInt, ShaderAttributeType.Vec4, this.UIVec4.updater)

    val UByte = ShaderInputAttributeDescriptor<UByte>(ShaderAttributePrimitive.UByte, ShaderAttributeType.UInt) { buffer, i, value ->
        buffer.put(i, value.toByte())
    }
    val NormalizedUByte = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UByte, ShaderAttributeType.Float, this.UByte.updater)
    val UBVec2 = ShaderInputAttributeDescriptor<UBVec2>(ShaderAttributePrimitive.UByte, ShaderAttributeType.UIVec2) { buffer, i, value ->
        buffer.put(i + 0, value.first.toByte())
        buffer.put(i + 1, value.second.toByte())
    }
    val NormalizedUBVec2 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UByte, ShaderAttributeType.Vec2, this.UBVec2.updater)
    val UBVec3 = ShaderInputAttributeDescriptor<UBVec3>(ShaderAttributePrimitive.UByte, ShaderAttributeType.UIVec3) { buffer, i, value ->
        buffer.put(i + 0, value.first.toByte())
        buffer.put(i + 1, value.second.toByte())
        buffer.put(i + 2, value.third.toByte())
    }
    val NormalizedUBVec3 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UByte, ShaderAttributeType.Vec3, this.UBVec3.updater)
    val UBVec4 = ShaderInputAttributeDescriptor<UBVec4>(ShaderAttributePrimitive.UByte, ShaderAttributeType.UIVec4) { buffer, i, value ->
        buffer.put(i + 0, value.first.toByte())
        buffer.put(i + 1, value.second.toByte())
        buffer.put(i + 2, value.third.toByte())
        buffer.put(i + 3, value.fourth.toByte())
    }
    val NormalizedUBVec4 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UByte, ShaderAttributeType.Vec4, this.UBVec4.updater)

    val UShort = ShaderInputAttributeDescriptor<UShort>(ShaderAttributePrimitive.UShort, ShaderAttributeType.UInt) { buffer, i, value ->
        buffer.putShort(i, value.toShort())
    }
    val NormalizedUShort = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UShort, ShaderAttributeType.Float, this.UShort.updater)
    val USVec2 = ShaderInputAttributeDescriptor<USVec2>(ShaderAttributePrimitive.UShort, ShaderAttributeType.UIVec2) { buffer, i, value ->
        buffer.putShort(i + 0, value.first.toShort())
        buffer.putShort(i + 2, value.second.toShort())
    }
    val NormalizedUSVec2 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UShort, ShaderAttributeType.Vec2, this.USVec2.updater)
    val USVec3 = ShaderInputAttributeDescriptor<USVec3>(ShaderAttributePrimitive.UShort, ShaderAttributeType.UIVec3) { buffer, i, value ->
        buffer.putShort(i + 0, value.first.toShort())
        buffer.putShort(i + 2, value.second.toShort())
        buffer.putShort(i + 4, value.third.toShort())
    }
    val NormalizedUSVec3 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UShort, ShaderAttributeType.Vec3, this.USVec3.updater)
    val USVec4 = ShaderInputAttributeDescriptor<USVec4>(ShaderAttributePrimitive.UShort, ShaderAttributeType.UIVec4) { buffer, i, value ->
        buffer.putShort(i + 0, value.first.toShort())
        buffer.putShort(i + 2, value.second.toShort())
        buffer.putShort(i + 4, value.third.toShort())
        buffer.putShort(i + 6, value.fourth.toShort())
    }
    val NormalizedUSVec4 = ShaderInputAttributeDescriptor(ShaderAttributePrimitive.UShort, ShaderAttributeType.Vec4, this.USVec4.updater)

    val Float = ShaderInputAttributeDescriptor<Float>(ShaderAttributePrimitive.Float, ShaderAttributeType.Float) { buffer, i, value ->
        buffer.putFloat(i, value)
    }
    val Vec2 = ShaderInputAttributeDescriptor<Vec2>(ShaderAttributePrimitive.Float, ShaderAttributeType.Vec2) { buffer, i, value ->
        buffer.putFloat(i + 0, value.first)
        buffer.putFloat(i + 4, value.second)
    }
    val Vec3 = ShaderInputAttributeDescriptor<Vec3>(ShaderAttributePrimitive.Float, ShaderAttributeType.Vec3) { buffer, i, value ->
        buffer.putFloat(i + 0, value.first)
        buffer.putFloat(i + 4, value.second)
        buffer.putFloat(i + 8, value.third)
    }
    val Vec4 = ShaderInputAttributeDescriptor<Vec4>(ShaderAttributePrimitive.Float, ShaderAttributeType.Vec4) { buffer, i, value ->
        buffer.putFloat(i + 0 , value.first)
        buffer.putFloat(i + 4 , value.second)
        buffer.putFloat(i + 8 , value.third)
        buffer.putFloat(i + 12, value.fourth)
    }

    val HalfFloat = ShaderInputAttributeDescriptor<Half>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Float) { buffer, i, value ->
        buffer.putShort(i, value.bits.toShort())
    }
    val HVec2 = ShaderInputAttributeDescriptor<HVec2>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Vec2) { buffer, i, value ->
        buffer.putShort(i + 0, value.first.bits.toShort())
        buffer.putShort(i + 2, value.second.bits.toShort())
    }
    val HVec3 = ShaderInputAttributeDescriptor<HVec3>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Vec3) { buffer, i, value ->
        buffer.putShort(i + 0, value.first.bits.toShort())
        buffer.putShort(i + 2, value.second.bits.toShort())
        buffer.putShort(i + 4, value.third.bits.toShort())
    }
    val HVec4 = ShaderInputAttributeDescriptor<HVec4>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Vec4) { buffer, i, value ->
        buffer.putShort(i + 0, value.first.bits.toShort())
        buffer.putShort(i + 2, value.second.bits.toShort())
        buffer.putShort(i + 4, value.third.bits.toShort())
        buffer.putShort(i + 6, value.fourth.bits.toShort())
    }

    val Mat2 = ShaderInputAttributeDescriptor<Mat2>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat2) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }
    val Mat2x3 = ShaderInputAttributeDescriptor<Mat2x3>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat2x3) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }
    val Mat2x4 = ShaderInputAttributeDescriptor<Mat2x4>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat2x4) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }
    val Mat3x2 = ShaderInputAttributeDescriptor<Mat3x2>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat3x2) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }
    val Mat3 = ShaderInputAttributeDescriptor<Mat3>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat3) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }
    val Mat3x4 = ShaderInputAttributeDescriptor<Mat3x4>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat3x4) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }
    val Mat4x2 = ShaderInputAttributeDescriptor<Mat4x2>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat4x2) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }
    val Mat4x3 = ShaderInputAttributeDescriptor<Mat4x3>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat4x3) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }
    val Mat4 = ShaderInputAttributeDescriptor<Mat4>(ShaderAttributePrimitive.Float, ShaderAttributeType.Mat4) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putFloat(startI + i * 4, value) }
    }

    val HMat2 = ShaderInputAttributeDescriptor<HMat2>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat2) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
    val HMat2x3 = ShaderInputAttributeDescriptor<HMat2x3>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat2x3) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
    val HMat2x4 = ShaderInputAttributeDescriptor<HMat2x4>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat2x4) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
    val HMat3x2 = ShaderInputAttributeDescriptor<HMat3x2>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat3x2) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
    val HMat3 = ShaderInputAttributeDescriptor<HMat3>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat3) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
    val HMat3x4 = ShaderInputAttributeDescriptor<HMat3x4>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat3x4) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
    val HMat4x2 = ShaderInputAttributeDescriptor<HMat4x2>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat4x2) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
    val HMat4x3 = ShaderInputAttributeDescriptor<HMat4x3>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat4x3) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
    val HMat4 = ShaderInputAttributeDescriptor<HMat4>(ShaderAttributePrimitive.HalfFloat, ShaderAttributeType.Mat4) { buffer, startI, value ->
        value.toFlatArray().forEachIndexed { i, value -> buffer.putShort(startI + i * 2, value.bits.toShort()) }
    }
}

public class ShaderVertexAttributeHandle<T> private constructor(
    public val name: String,
    descriptor: ShaderInputAttributeDescriptor<T>, public val normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE
) {
    public val primitive: ShaderAttributePrimitive = descriptor.primitive
    public val type: ShaderAttributeType = descriptor.type
    internal val updater: (ByteBuffer, Int, T) -> Unit = descriptor.updater
    public companion object {
        public fun int(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<Int> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.Int } else { ShaderInputAttributeDescriptors.NormalizedInt },
            normalization
        )
        public fun iVec2(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<IVec2> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.IVec2 } else { ShaderInputAttributeDescriptors.NormalizedIVec2 },
            normalization
        )
        public fun iVec3(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<IVec3> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.IVec3 } else { ShaderInputAttributeDescriptors.NormalizedIVec3 },
            normalization
        )
        public fun iVec4(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<IVec4> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.IVec4 } else { ShaderInputAttributeDescriptors.NormalizedIVec4 },
            normalization
        )
        public fun byte(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<Byte> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.Byte } else { ShaderInputAttributeDescriptors.NormalizedByte },
            normalization
        )
        public fun bVec2(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<BVec2> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.BVec2 } else { ShaderInputAttributeDescriptors.NormalizedBVec2 },
            normalization
        )
        public fun bVec3(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<BVec3> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.BVec3 } else { ShaderInputAttributeDescriptors.NormalizedBVec3 },
            normalization
        )
        public fun bVec4(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<BVec4> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.BVec4 } else { ShaderInputAttributeDescriptors.NormalizedBVec4 },
            normalization
        )

        public fun uint(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<UInt> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UInt } else { ShaderInputAttributeDescriptors.NormalizedUInt },
            normalization
        )
        public fun uiVec2(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<UIVec2> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UIVec2 } else { ShaderInputAttributeDescriptors.NormalizedUIVec2 },
            normalization
        )
        public fun uiVec3(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<UIVec3> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UIVec3 } else { ShaderInputAttributeDescriptors.NormalizedUIVec3 },
            normalization
        )
        public fun uiVec4(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<UIVec4> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UIVec4 } else { ShaderInputAttributeDescriptors.NormalizedUIVec4 },
            normalization
        )
        public fun ubyte(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<UByte> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UByte } else { ShaderInputAttributeDescriptors.NormalizedUByte },
            normalization
        )
        public fun ubVec2(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<UBVec2> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UBVec2 } else { ShaderInputAttributeDescriptors.NormalizedUBVec2 },
            normalization
        )
        public fun ubVec3(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<UBVec3> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UBVec3 } else { ShaderInputAttributeDescriptors.NormalizedUBVec3 },
            normalization
        )
        public fun ubVec4(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderVertexAttributeHandle<UBVec4> = ShaderVertexAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UBVec4 } else { ShaderInputAttributeDescriptors.NormalizedUBVec4 },
            normalization
        )

        public fun float(name: String): ShaderVertexAttributeHandle<Float> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Float)
        public fun vec2(name: String): ShaderVertexAttributeHandle<Vec2> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Vec2)
        public fun vec3(name: String): ShaderVertexAttributeHandle<Vec3> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Vec3)
        public fun vec4(name: String): ShaderVertexAttributeHandle<Vec4> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Vec4)
        public fun half(name: String): ShaderVertexAttributeHandle<Half> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HalfFloat)
        public fun hVec2(name: String): ShaderVertexAttributeHandle<HVec2> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HVec2)
        public fun hVec3(name: String): ShaderVertexAttributeHandle<HVec3> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HVec3)
        public fun hVec4(name: String): ShaderVertexAttributeHandle<HVec4> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HVec4)
        public fun mat2(name: String): ShaderVertexAttributeHandle<Mat2> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat2)
        public fun mat2x3(name: String): ShaderVertexAttributeHandle<Mat2x3> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat2x3)
        public fun mat2x4(name: String): ShaderVertexAttributeHandle<Mat2x4> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat2x4)
        public fun mat3x2(name: String): ShaderVertexAttributeHandle<Mat3x2> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat3x2)
        public fun mat3(name: String): ShaderVertexAttributeHandle<Mat3> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat3)
        public fun mat3x4(name: String): ShaderVertexAttributeHandle<Mat3x4> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat3x4)
        public fun mat4x2(name: String): ShaderVertexAttributeHandle<Mat4x2> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat4x2)
        public fun mat4x3(name: String): ShaderVertexAttributeHandle<Mat4x3> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat4x3)
        public fun mat4(name: String): ShaderVertexAttributeHandle<Mat4> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.Mat4)
        public fun hMat2(name: String): ShaderVertexAttributeHandle<HMat2> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat2)
        public fun hMat2x3(name: String): ShaderVertexAttributeHandle<HMat2x3> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat2x3)
        public fun hMat2x4(name: String): ShaderVertexAttributeHandle<HMat2x4> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat2x4)
        public fun hMat3x2(name: String): ShaderVertexAttributeHandle<HMat3x2> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat3x2)
        public fun hMat3(name: String): ShaderVertexAttributeHandle<HMat3> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat3)
        public fun hMat3x4(name: String): ShaderVertexAttributeHandle<HMat3x4> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat3x4)
        public fun hMat4x2(name: String): ShaderVertexAttributeHandle<HMat4x2> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat4x2)
        public fun hMat4x3(name: String): ShaderVertexAttributeHandle<HMat4x3> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat4x3)
        public fun hMat4(name: String): ShaderVertexAttributeHandle<HMat4> = ShaderVertexAttributeHandle(name, ShaderInputAttributeDescriptors.HMat4)
    }

    override fun toString(): String = this.name
}

public class ShaderInstanceAttributeHandle<T> private constructor(
    public val name: String,
    descriptor: ShaderInputAttributeDescriptor<T>, public val normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE
) {
    public val primitive: ShaderAttributePrimitive = descriptor.primitive
    public val type: ShaderAttributeType = descriptor.type
    internal val updater: (ByteBuffer, Int, T) -> Unit = descriptor.updater
    public companion object {
        public fun int(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<Int> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.Int } else { ShaderInputAttributeDescriptors.NormalizedInt },
            normalization
        )
        public fun iVec2(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<IVec2> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.IVec2 } else { ShaderInputAttributeDescriptors.NormalizedIVec2 },
            normalization
        )
        public fun iVec3(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<IVec3> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.IVec3 } else { ShaderInputAttributeDescriptors.NormalizedIVec3 },
            normalization
        )
        public fun iVec4(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<IVec4> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.IVec4 } else { ShaderInputAttributeDescriptors.NormalizedIVec4 },
            normalization
        )
        public fun byte(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<Byte> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.Byte } else { ShaderInputAttributeDescriptors.NormalizedByte },
            normalization
        )
        public fun bVec2(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<BVec2> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.BVec2 } else { ShaderInputAttributeDescriptors.NormalizedBVec2 },
            normalization
        )
        public fun bVec3(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<BVec3> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.BVec3 } else { ShaderInputAttributeDescriptors.NormalizedBVec3 },
            normalization
        )
        public fun bVec4(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<BVec4> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.BVec4 } else { ShaderInputAttributeDescriptors.NormalizedBVec4 },
            normalization
        )

        public fun uint(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<UInt> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UInt } else { ShaderInputAttributeDescriptors.NormalizedUInt },
            normalization
        )
        public fun uiVec2(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<UIVec2> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UIVec2 } else { ShaderInputAttributeDescriptors.NormalizedUIVec2 },
            normalization
        )
        public fun uiVec3(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<UIVec3> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UIVec3 } else { ShaderInputAttributeDescriptors.NormalizedUIVec3 },
            normalization
        )
        public fun uiVec4(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<UIVec4> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UIVec4 } else { ShaderInputAttributeDescriptors.NormalizedUIVec4 },
            normalization
        )
        public fun ubyte(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<UByte> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UByte } else { ShaderInputAttributeDescriptors.NormalizedUByte },
            normalization
        )
        public fun ubVec2(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<UBVec2> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UBVec2 } else { ShaderInputAttributeDescriptors.NormalizedUBVec2 },
            normalization
        )
        public fun ubVec3(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<UBVec3> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UBVec3 } else { ShaderInputAttributeDescriptors.NormalizedUBVec3 },
            normalization
        )
        public fun ubVec4(name: String, normalization: ShaderAttributeNormalization = ShaderAttributeNormalization.NONE): ShaderInstanceAttributeHandle<UBVec4> = ShaderInstanceAttributeHandle(name,
            if (normalization.isNone()) { ShaderInputAttributeDescriptors.UBVec4 } else { ShaderInputAttributeDescriptors.NormalizedUBVec4 },
            normalization
        )

        public fun float(name: String): ShaderInstanceAttributeHandle<Float> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Float)
        public fun vec2(name: String): ShaderInstanceAttributeHandle<Vec2> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Vec2)
        public fun vec3(name: String): ShaderInstanceAttributeHandle<Vec3> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Vec3)
        public fun vec4(name: String): ShaderInstanceAttributeHandle<Vec4> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Vec4)
        public fun half(name: String): ShaderInstanceAttributeHandle<Half> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HalfFloat)
        public fun hVec2(name: String): ShaderInstanceAttributeHandle<HVec2> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HVec2)
        public fun hVec3(name: String): ShaderInstanceAttributeHandle<HVec3> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HVec3)
        public fun hVec4(name: String): ShaderInstanceAttributeHandle<HVec4> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HVec4)
        public fun mat2(name: String): ShaderInstanceAttributeHandle<Mat2> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat2)
        public fun mat2x3(name: String): ShaderInstanceAttributeHandle<Mat2x3> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat2x3)
        public fun mat2x4(name: String): ShaderInstanceAttributeHandle<Mat2x4> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat2x4)
        public fun mat3x2(name: String): ShaderInstanceAttributeHandle<Mat3x2> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat3x2)
        public fun mat3(name: String): ShaderInstanceAttributeHandle<Mat3> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat3)
        public fun mat3x4(name: String): ShaderInstanceAttributeHandle<Mat3x4> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat3x4)
        public fun mat4x2(name: String): ShaderInstanceAttributeHandle<Mat4x2> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat4x2)
        public fun mat4x3(name: String): ShaderInstanceAttributeHandle<Mat4x3> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat4x3)
        public fun mat4(name: String): ShaderInstanceAttributeHandle<Mat4> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.Mat4)
        public fun hMat2(name: String): ShaderInstanceAttributeHandle<HMat2> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat2)
        public fun hMat2x3(name: String): ShaderInstanceAttributeHandle<HMat2x3> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat2x3)
        public fun hMat2x4(name: String): ShaderInstanceAttributeHandle<HMat2x4> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat2x4)
        public fun hMat3x2(name: String): ShaderInstanceAttributeHandle<HMat3x2> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat3x2)
        public fun hMat3(name: String): ShaderInstanceAttributeHandle<HMat3> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat3)
        public fun hMat3x4(name: String): ShaderInstanceAttributeHandle<HMat3x4> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat3x4)
        public fun hMat4x2(name: String): ShaderInstanceAttributeHandle<HMat4x2> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat4x2)
        public fun hMat4x3(name: String): ShaderInstanceAttributeHandle<HMat4x3> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat4x3)
        public fun hMat4(name: String): ShaderInstanceAttributeHandle<HMat4> = ShaderInstanceAttributeHandle(name, ShaderInputAttributeDescriptors.HMat4)
    }

    override fun toString(): String = this.name
}
