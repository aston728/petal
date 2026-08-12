package com.aston728.engine.core.rendererBase

public class ShaderAttributeInfo(public val name: String, public val type: ShaderAttributeType)

public class ShaderVertexAttributeInfo<T>(public val name: String, public val handle: ShaderVertexAttributeHandle<T>)
public class ShaderInstanceAttributeInfo<T>(public val name: String, public val handle: ShaderInstanceAttributeHandle<T>)

public class ShaderUniformInfo<T>(
    public val name: String, public val handle: ShaderUniformHandle<T>,
    public val defaultValue: T
) {
    internal fun applyDefault(shader: Shader): Unit {
        shader.setUniform(this.handle, this.defaultValue)
    }
}
