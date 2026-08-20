package com.aston728.engine.core.rendererBase

public enum class ShaderUniformType(public val isSampler: Boolean = false) {
    Int, IVec2, IVec3, IVec4,
    Bool, BoolVec2, BoolVec3, BoolVec4,
    UInt, UIVec2, UIVec3, UIVec4,
    Float, Vec2, Vec3, Vec4,
    Mat2, TMat2, Mat2x3, TMat2x3, Mat2x4, TMat2x4,
    Mat3x2, TMat3x2, Mat3, TMat3, Mat3x4, TMat3x4,
    Mat4x2, TMat4x2, Mat4x3, TMat4x3, Mat4, TMat4,
    Image2DSampler(true), Image2DArraySampler(true),
}
