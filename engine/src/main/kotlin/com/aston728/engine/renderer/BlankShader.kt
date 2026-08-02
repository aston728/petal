package com.aston728.engine.renderer

internal class BlankShader(name: String) : Shader(name) {
    override fun isValid(): Boolean = false

    override fun addInstance(): Unit {}
    override fun removeInstance(i: Int): Unit {}
    override fun <T> setVertexAttribute(handle: ShaderVertexAttributeHandle<T>, instanceI: Int, values: Array<T>) {}
    override fun <T> setInstanceAttribute(handle: ShaderInstanceAttributeHandle<T>, instanceI: Int, value: T) {}
    override fun <T> setUniform(handle: ShaderUniformHandle<T>, value: T) {}

    override fun destroy() {}
    override fun draw() {}
}
