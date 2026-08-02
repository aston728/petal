package com.aston728.engine.renderer

class ShaderInstanceHandle internal constructor(internal val shader: Shader, internal var instanceI: Int) {
    fun <T> setVertexAttribute(handle: ShaderVertexAttributeHandle<T>, values: Array<T>): ShaderInstanceHandle = apply {
        this.shader.setVertexAttribute(handle, this.instanceI, values)
    }
    fun <T> setInstanceAttribute(handle: ShaderInstanceAttributeHandle<T>, value: T): ShaderInstanceHandle = apply {
        this.shader.setInstanceAttribute(handle, this.instanceI, value)
    }

    internal fun draw(): Unit { this.shader.draw() } // TODO
}
