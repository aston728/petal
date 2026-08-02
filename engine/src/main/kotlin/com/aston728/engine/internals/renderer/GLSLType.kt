package com.aston728.engine.internals.renderer

enum class GLSLType(private val type: String) {
    Int("int"),
    IVec2("ivec2"),
    IVec3("ivec3"),
    IVec4("ivec4"),

    UInt("uint"),
    UIVec2("uvec2"),
    UIVec3("uvec3"),
    UIVec4("uvec4"),

    Float("float"),
    Vec2("vec2"),
    Vec3("vec3"),
    Vec4("vec4"),

    Mat2("mat2"),
    Mat3("mat3"),
    Mat4("mat4"),
    Mat2x3("mat2x3"),
    Mat2x4("mat2x4"),
    Mat3x2("mat3x2"),
    Mat3x4("mat3x4"),
    Mat4x2("mat4x2"),
    Mat4x3("mat4x3");

    override fun toString(): String = this.type
}
