package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.types.ErrorHandler

public abstract class ShaderBuilder {
    protected abstract fun onBuild(spec: ShaderSpec, errorCallback: ErrorHandler): Shader?
    internal fun build(spec: ShaderSpec, errorCallback: ErrorHandler): Shader? = this.onBuild(spec, errorCallback)
}
