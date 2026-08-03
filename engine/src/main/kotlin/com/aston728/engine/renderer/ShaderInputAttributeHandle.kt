package com.aston728.engine.renderer

import com.aston728.engine.internals.renderer.GLSLAttributeType
import com.aston728.engine.types.IVec2
import com.aston728.engine.types.IVec3
import com.aston728.engine.types.IVec4

import com.aston728.engine.types.Vec2
import com.aston728.engine.types.Vec3
import com.aston728.engine.types.Vec4

internal class ShaderInputAttributeDescriptor<T>(val type: GLSLAttributeType, val updater: (FloatArray, Int, T) -> Unit)
private object ShaderInputAttributeDescriptors {
    val INT: ShaderInputAttributeDescriptor<Int> = ShaderInputAttributeDescriptor(GLSLAttributeType.Int) {
        array, i, value -> array[i] = value.toFloat()
    }
    val IVEC2: ShaderInputAttributeDescriptor<IVec2> = ShaderInputAttributeDescriptor(GLSLAttributeType.IVec2) { array, i, value ->
        array[i] = value.first.toFloat()
        array[i + 1] = value.second.toFloat()
    }
    val IVEC3: ShaderInputAttributeDescriptor<IVec3> = ShaderInputAttributeDescriptor(GLSLAttributeType.IVec3) { array, i, value ->
        array[i] = value.first.toFloat()
        array[i + 1] = value.second.toFloat()
        array[i + 2] = value.third.toFloat()
    }
    val IVEC4: ShaderInputAttributeDescriptor<IVec4> = ShaderInputAttributeDescriptor(GLSLAttributeType.IVec4) { array, i, value ->
        array[i] = value.first.toFloat()
        array[i + 1] = value.second.toFloat()
        array[i + 2] = value.third.toFloat()
        array[i + 3] = value.fourth.toFloat()
    }
    val FLOAT: ShaderInputAttributeDescriptor<Float> = ShaderInputAttributeDescriptor(GLSLAttributeType.Float) {
        array, i, value -> array[i] = value
    }
    val VEC2: ShaderInputAttributeDescriptor<Vec2> = ShaderInputAttributeDescriptor(GLSLAttributeType.Vec2) { array, i, value ->
        array[i] = value.first
        array[i + 1] = value.second
    }
    val VEC3: ShaderInputAttributeDescriptor<Vec3> = ShaderInputAttributeDescriptor(GLSLAttributeType.Vec3) { array, i, value ->
        array[i] = value.first
        array[i + 1] = value.second
        array[i + 2] = value.third
    }
    val VEC4: ShaderInputAttributeDescriptor<Vec4> = ShaderInputAttributeDescriptor(GLSLAttributeType.Vec4) { array, i, value ->
        array[i] = value.first
        array[i + 1] = value.second
        array[i + 2] = value.third
        array[i + 3] = value.fourth
    }
}

class ShaderVertexAttributeHandle<T> private constructor(
    internal val descriptor: ShaderInputAttributeDescriptor<T>, internal val isNormalized: Boolean = false
) {
    companion object {
        fun int(): ShaderVertexAttributeHandle<Int> = ShaderVertexAttributeHandle(ShaderInputAttributeDescriptors.INT)
        fun ivec2(): ShaderVertexAttributeHandle<IVec2> = ShaderVertexAttributeHandle(ShaderInputAttributeDescriptors.IVEC2)
        fun ivec3(): ShaderVertexAttributeHandle<IVec3> = ShaderVertexAttributeHandle(ShaderInputAttributeDescriptors.IVEC3)
        fun ivec4(): ShaderVertexAttributeHandle<IVec4> = ShaderVertexAttributeHandle(ShaderInputAttributeDescriptors.IVEC4)
        fun float(): ShaderVertexAttributeHandle<Float> = ShaderVertexAttributeHandle(ShaderInputAttributeDescriptors.FLOAT)
        fun vec2(): ShaderVertexAttributeHandle<Vec2> = ShaderVertexAttributeHandle(ShaderInputAttributeDescriptors.VEC2)
        fun vec3(): ShaderVertexAttributeHandle<Vec3> = ShaderVertexAttributeHandle(ShaderInputAttributeDescriptors.VEC3)
        fun vec4(): ShaderVertexAttributeHandle<Vec4> = ShaderVertexAttributeHandle(ShaderInputAttributeDescriptors.VEC4)
    }
}
class ShaderInstanceAttributeHandle<T> private constructor(
    internal val descriptor: ShaderInputAttributeDescriptor<T>, internal val isNormalized: Boolean = false
) {
    companion object {
        fun int(): ShaderInstanceAttributeHandle<Int> = ShaderInstanceAttributeHandle(ShaderInputAttributeDescriptors.INT)
        fun ivec2(): ShaderInstanceAttributeHandle<IVec2> = ShaderInstanceAttributeHandle(ShaderInputAttributeDescriptors.IVEC2)
        fun ivec3(): ShaderInstanceAttributeHandle<IVec3> = ShaderInstanceAttributeHandle(ShaderInputAttributeDescriptors.IVEC3)
        fun ivec4(): ShaderInstanceAttributeHandle<IVec4> = ShaderInstanceAttributeHandle(ShaderInputAttributeDescriptors.IVEC4)
        fun float(): ShaderInstanceAttributeHandle<Float> = ShaderInstanceAttributeHandle(ShaderInputAttributeDescriptors.FLOAT)
        fun vec2(): ShaderInstanceAttributeHandle<Vec2> = ShaderInstanceAttributeHandle(ShaderInputAttributeDescriptors.VEC2)
        fun vec3(): ShaderInstanceAttributeHandle<Vec3> = ShaderInstanceAttributeHandle(ShaderInputAttributeDescriptors.VEC3)
        fun vec4(): ShaderInstanceAttributeHandle<Vec4> = ShaderInstanceAttributeHandle(ShaderInputAttributeDescriptors.VEC4)
    }
}
