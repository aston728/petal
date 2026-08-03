package com.aston728.engine.renderer

import com.aston728.engine.internals.renderer.GLSLAttributeType

internal class ShaderAttributeInfo(internal val name: String, internal val type: GLSLAttributeType)

internal class ShaderVertexAttributeInfo<T>(internal val name: String, internal val handle: ShaderVertexAttributeHandle<T>)
internal class ShaderInstanceAttributeInfo<T>(internal val name: String, internal val handle: ShaderInstanceAttributeHandle<T>)

internal class ShaderUniformInfo<T>(internal val name: String, internal val handle: ShaderUniformHandle<T>, internal val defaultValue: T) {
    internal fun applyDefault(shader: Shader): Unit {
        shader.setUniform(this.handle, this.defaultValue)
    }
}
