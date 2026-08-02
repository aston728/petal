package com.aston728.engine.renderer

import com.aston728.engine.utils.NamedObject

abstract class Shader(name: String) : NamedObject<Shader>(name) {
    abstract fun isValid(): Boolean

    internal abstract fun addInstance(): Unit
    internal abstract fun removeInstance(i: Int): Unit
    internal abstract fun <T> setVertexAttribute(handle: ShaderVertexAttributeHandle<T>, instanceI: Int, values: Array<T>): Unit
    internal abstract fun <T> setInstanceAttribute(handle: ShaderInstanceAttributeHandle<T>, instanceI: Int, value: T): Unit
    abstract fun <T> setUniform(handle: ShaderUniformHandle<T>, value: T): Unit

    internal abstract fun destroy(): Unit
    internal abstract fun draw(): Unit
}
