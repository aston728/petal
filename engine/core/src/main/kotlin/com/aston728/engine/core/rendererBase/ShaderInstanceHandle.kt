package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.NamedObject

public class ShaderInstanceHandle internal constructor(
    public val shader: Shader, internal val mesh: Mesh, internal val material: Material, internal val instanceI: Int,
    private val errorCallback: (String) -> Unit
) : NamedObject<ShaderInstanceHandle>("Unnamed Shader Instance Handle") {
    public var isValid: Boolean = true
        private set

    internal fun invalidate(): Unit {
        this.isValid = false
    }

    public fun <T> setVertexAttribute(handle: ShaderVertexAttributeHandle<T>, values: List<T>): ShaderInstanceHandle = apply {
        if (!this.isValid) {
            this.errorCallback("Cannot set vertex attribute, $this is invalid.")
        } else {
            this.mesh.setVertexAttribute(handle, handle.updater, values)
        }
    }
    public fun <T> setInstanceAttribute(handle: ShaderInstanceAttributeHandle<T>, value: T): ShaderInstanceHandle = apply {
        if (!this.isValid) {
            this.errorCallback("Cannot set vertex attribute, $this is invalid.")
        } else {
            this.mesh.setInstanceAttribute(handle, handle.updater, this.instanceI, value)
        }
    }
}
