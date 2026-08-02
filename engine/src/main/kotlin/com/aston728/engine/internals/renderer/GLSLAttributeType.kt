package com.aston728.engine.internals.renderer

enum class GLSLAttributeType(
    private val type: GLSLType, private val componentType: GLSLPrimitive,
    private val numLocations: Int, private val componentsPerLocation: Int
) {
    Int(GLSLType.Int, GLSLPrimitive.INT, 1, 1),
    IVec2(GLSLType.IVec2, GLSLPrimitive.INT, 1, 2),
    IVec3(GLSLType.IVec3, GLSLPrimitive.INT, 1, 3),
    IVec4(GLSLType.IVec4, GLSLPrimitive.INT, 1, 4),

    UInt(GLSLType.UInt, GLSLPrimitive.UINT, 1, 1),
    UIVec2(GLSLType.UIVec2, GLSLPrimitive.UINT, 1, 2),
    UIVec3(GLSLType.UIVec3, GLSLPrimitive.UINT, 1, 3),
    UIVec4(GLSLType.UIVec4, GLSLPrimitive.UINT, 1, 4),

    Float(GLSLType.Float, GLSLPrimitive.FLOAT, 1, 1),
    Vec2(GLSLType.Vec2, GLSLPrimitive.FLOAT, 1, 2),
    Vec3(GLSLType.Vec3, GLSLPrimitive.FLOAT, 1, 3),
    Vec4(GLSLType.Vec4, GLSLPrimitive.FLOAT, 1, 4),

    Mat2(GLSLType.Mat2, GLSLPrimitive.FLOAT, 2, 2),
    Mat3(GLSLType.Mat3, GLSLPrimitive.FLOAT, 3, 3),
    Mat4(GLSLType.Mat4, GLSLPrimitive.FLOAT, 4, 4),
    Mat2x3(GLSLType.Mat2x3, GLSLPrimitive.FLOAT, 2, 3),
    Mat2x4(GLSLType.Mat2x4, GLSLPrimitive.FLOAT, 2, 4),
    Mat3x2(GLSLType.Mat3x2, GLSLPrimitive.FLOAT, 3, 2),
    Mat3x4(GLSLType.Mat3x4, GLSLPrimitive.FLOAT, 3, 4),
    Mat4x2(GLSLType.Mat4x2, GLSLPrimitive.FLOAT, 4, 2),
    Mat4x3(GLSLType.Mat4x3, GLSLPrimitive.FLOAT, 4, 3);

    internal fun getComponentType(): GLSLPrimitive = this.componentType
    internal fun getNumLocations(): Int = this.numLocations
    internal fun getComponentsPerLocation(): Int = this.componentsPerLocation
    internal fun getNumComponents(): Int = this.componentsPerLocation * this.numLocations
    internal fun getLocationByteSize(): Int = this.componentType.getByteSize() * this.componentsPerLocation
    internal fun getByteSize(): Int = this.componentType.getByteSize() * this.componentsPerLocation * this.numLocations

    override fun toString(): String = this.type.toString()
}
