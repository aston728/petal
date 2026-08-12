package com.aston728.engine.core.rendererBase

public sealed class ShaderData {
   internal open fun applyTo(shaderInstanceHandle: ShaderInstanceHandle): Unit {}
}
public class ShaderVertexData<T>(public val handle: ShaderVertexAttributeHandle<T>, public val values: Array<T>) : ShaderData() {
    override fun applyTo(shaderInstanceHandle: ShaderInstanceHandle): Unit {
        shaderInstanceHandle.setVertexAttribute(this.handle, this.values)
    }
}
public class ShaderInstanceData<T>(public val handle: ShaderInstanceAttributeHandle<T>, public val value: T) : ShaderData() {
    override fun applyTo(shaderInstanceHandle: ShaderInstanceHandle): Unit {
        shaderInstanceHandle.setInstanceAttribute(this.handle, this.value)
    }
}
