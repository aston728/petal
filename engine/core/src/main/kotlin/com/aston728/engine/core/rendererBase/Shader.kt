package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.Handle
import com.aston728.engine.core.utils.NamedObject
import java.nio.ByteBuffer

public abstract class Shader(name: String) : NamedObject<Shader>(name) {
    private val onDestroyHandlers: MutableList<(Shader) -> Unit> = mutableListOf()

    public abstract fun exists(): Boolean
    public fun addOnDestroyHandler(handler: (Shader) -> Unit, handle: Handle? = null): Unit {
        val wrapper: (Shader) -> Unit = { shader -> handler(shader) }
        this.onDestroyHandlers.add(wrapper)
        handle?.setOnRemoveHandler { this.onDestroyHandlers.remove(wrapper) }
    }

    protected abstract fun computeHasInstances(): Boolean
    protected abstract fun onAddInstance(): Int
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
    protected abstract fun onBind(): Unit
    protected abstract fun onDestroy(): Unit

    internal fun hasInstances(): Boolean = this.computeHasInstances()
    internal fun addInstance(): Int = this.onAddInstance()
    internal fun removeInstance(i: Int): Unit = this.onRemoveInstance(i)
    internal fun <T> setVertexAttribute(
        handle: ShaderVertexAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        values: Array<T>
    ): Unit = this.onSetVertexAttribute(handle, updater, instanceI, values)
    internal fun <T> setInstanceAttribute(
        handle: ShaderInstanceAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        value: T
    ): Unit = this.onSetInstanceAttribute(handle, updater, instanceI, value)
    internal fun bind(): Unit = this.onBind()
    internal fun destroy(): Unit {
        this.onDestroy()
        this.onDestroyHandlers.toList().forEach { it(this) }
    }
}
