package com.aston728.engine.core.rendererBase

public enum class ShaderAttributeType(public val numComponents: Int) {
    Int(1), IVec2(2), IVec3(3), IVec4(4),
    UInt(1), UIVec2(2), UIVec3(3), UIVec4(4),
    Float(1), Vec2(2), Vec3(3), Vec4(4),
    Mat2(4), Mat2x3(6), Mat2x4(8), Mat3x2(6), Mat3(9), Mat3x4(12), Mat4x2(8), Mat4x3(12), Mat4(16),
}

public enum class ShaderAttributePrimitive(public val byteSize: Int) {
    Int(4), Byte(1), Short(2),
    UInt(4), UByte(1), UShort(2),
    Float(4), HalfFloat(2),
}

public enum class ShaderAttributeNormalization {
    NONE, CAST, NORMALIZED;
    public fun isNone(): Boolean = this == ShaderAttributeNormalization.NONE
}
