package com.aston728.engine.renderer

sealed interface ShaderData {
   fun applyTo(shaderInstanceHandle: ShaderInstanceHandle)
}
class ShaderVertexData<T>(val handle: ShaderVertexAttributeHandle<T>, val values: Array<T>) : ShaderData {
    override fun applyTo(shaderInstanceHandle: ShaderInstanceHandle) {
        shaderInstanceHandle.setVertexAttribute(this.handle, this.values)
    }
}
class ShaderInstanceData<T>(val handle: ShaderInstanceAttributeHandle<T>, val value: T) : ShaderData {
    override fun applyTo(shaderInstanceHandle: ShaderInstanceHandle) {
        shaderInstanceHandle.setInstanceAttribute(this.handle, this.value)
    }
}
