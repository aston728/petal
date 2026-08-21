package com.aston728.engine.core.rendererBase

internal interface ShaderProvider {
    fun specGetOrMerge(vararg specs: ShaderSpec): ShaderSpec
    fun acquire(windowHandle: Long, spec: ShaderSpec): ShaderInstanceHandle?
    fun release(handle: ShaderInstanceHandle): Unit
}
