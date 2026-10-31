package com.aston728.engine.core.rendererBase

public class ShaderInstanceData<T>(public val handle: ShaderInstanceAttributeHandle<T>, public val value: T) {
    internal fun applyTo(shaderInstanceHandle: ShaderInstanceHandle): Unit {
        shaderInstanceHandle.setInstanceAttribute(this.handle, this.value)
    }
}
