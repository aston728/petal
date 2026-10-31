package com.aston728.engine.core.rendererBase

public class ShaderAttributeInfo(public val name: String, public val type: ShaderAttributeType)

public class ShaderVertexAttributeInfo<T>(public val handle: ShaderVertexAttributeHandle<T>, public val defaultValue: List<T>) {
    internal fun applyDefault(mesh: Mesh): Unit {
        mesh.setVertexAttribute(this.handle, this.handle.updater, this.defaultValue)
    }
}
public class ShaderUniformInfo<T>(public val handle: ShaderUniformHandle<T>, public val defaultValue: T)
