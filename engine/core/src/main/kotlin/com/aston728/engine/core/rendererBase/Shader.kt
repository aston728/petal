package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.NamedObject
import java.nio.ByteBuffer

public abstract class Shader(name: String) : NamedObject<Shader>(name) {
    public abstract fun isValid(): Boolean

    protected abstract fun onAddInstance(): Unit
    protected abstract fun onRemoveInstance(i: Int): Unit
    protected abstract fun <T> onSetVertexAttribute(
        handle: ShaderVertexAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        values: Array<T>
    ): Unit
    protected abstract fun <T> onSetInstanceAttribute(
        handle: ShaderInstanceAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        value: T
    ): Unit
    public abstract fun <T> setUniform(handle: ShaderUniformHandle<T>, value: T): Unit
    protected abstract fun onDestroy(): Unit
    protected abstract fun onDraw(graphicsContext: GraphicsContext): Unit

    internal fun addInstance(): Unit = this.onAddInstance()
    internal fun removeInstance(i: Int): Unit = this.onRemoveInstance(i)
    internal fun <T> setVertexAttribute(
        handle: ShaderVertexAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        values: Array<T>
    ): Unit = this.onSetVertexAttribute(handle, updater, instanceI, values)
    internal fun <T> setInstanceAttribute(
        handle: ShaderInstanceAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        value: T
    ): Unit = this.onSetInstanceAttribute(handle, updater, instanceI, value)
    internal fun destroy(): Unit = this.onDestroy()
    internal fun draw(graphicsContext: GraphicsContext): Unit = this.onDraw(graphicsContext)
}
