package com.aston728.engine.renderer

import com.aston728.engine.internals.renderer.GLSLAttributeType

internal class ShaderAttributeInfo(val name: String, val type: GLSLAttributeType)

internal sealed class ShaderInputAttributeInfo<T>(internal val name: String, internal open val handle: ShaderInputAttributeHandle<T>) {
    internal var location: Int = -1
    internal var bufferLocation: Int = -1
}
internal class ShaderVertexAttributeInfo<T>(
    name: String, override val handle: ShaderVertexAttributeHandle<T>
) : ShaderInputAttributeInfo<T>(name, handle)
internal class ShaderInstanceAttributeInfo<T>(
    name: String, override val handle: ShaderInstanceAttributeHandle<T>
) : ShaderInputAttributeInfo<T>(name, handle)

internal class ShaderUniformInfo<T>(internal val name: String, internal val handle: ShaderUniformHandle<T>, internal val defaultValue: T) {
    internal fun applyDefault(shader: Shader): Unit {
        shader.setUniform(this.handle, this.defaultValue)
    }
}
