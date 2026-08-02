package com.aston728.engine.renderer

internal interface ShaderProvider {
    fun acquirePlaceholder(): ShaderInstanceHandle
    fun acquire(contextHandle: Long, spec: ShaderSpec): ShaderInstanceHandle
    fun release(handle: ShaderInstanceHandle): Unit
}
