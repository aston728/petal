package com.aston728.engine.core.rendererBase

import java.nio.ByteBuffer

internal class BlankShader(name: String) : Shader(name) {
    override fun isValid(): Boolean = false

    override fun onAddInstance(): Unit {}
    override fun onRemoveInstance(i: Int): Unit {}
    override fun <T> onSetVertexAttribute(
        handle: ShaderVertexAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        values: Array<T>
    ): Unit {}
    override fun <T> onSetInstanceAttribute(
        handle: ShaderInstanceAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        value: T
    ): Unit {}
    override fun <T> setUniform(handle: ShaderUniformHandle<T>, value: T): Unit {}

    override fun onDestroy(): Unit {}
    override fun onDraw(): Unit {}
}
