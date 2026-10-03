package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.NamedObject

public class ShaderInstanceHandle internal constructor(
    internal val shader: Shader, internal val instanceI: Int,
    private val errorCallback: (String) -> Unit,
) : NamedObject<ShaderInstanceHandle>("Unnamed Shader Instance Handle") {
    private var isInvalid: Boolean = false
    internal fun invalidate(): Unit {
        this.isInvalid = true
    }

    public fun <T> setVertexAttribute(handle: ShaderVertexAttributeHandle<T>, values: Array<T>): ShaderInstanceHandle = apply {
        if (this.isInvalid) {
            this.errorCallback("Cannot set vertex attribute, $this is invalid.")
        } else {
            this.shader.setVertexAttribute(handle, handle.updater, this.instanceI, values)
        }
    }
    public fun <T> setInstanceAttribute(handle: ShaderInstanceAttributeHandle<T>, value: T): ShaderInstanceHandle = apply {
        if (this.isInvalid) {
            this.errorCallback("Cannot set vertex attribute, $this is invalid.")
        } else {
            this.shader.setInstanceAttribute(handle, handle.updater, this.instanceI, value)
        }
    }
}
