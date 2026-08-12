package com.aston728.engine.core.rendererBase

public class ShaderInstanceHandle internal constructor(internal val shader: Shader, internal var instanceI: Int) {
    public fun <T> setVertexAttribute(handle: ShaderVertexAttributeHandle<T>, values: Array<T>): ShaderInstanceHandle = apply {
        this.shader.setVertexAttribute(handle, handle.updater, this.instanceI, values)
    }
    public fun <T> setInstanceAttribute(handle: ShaderInstanceAttributeHandle<T>, value: T): ShaderInstanceHandle = apply {
        this.shader.setInstanceAttribute(handle, handle.updater, this.instanceI, value)
    }

    internal fun draw(): Unit { this.shader.draw() } // TODO
}
