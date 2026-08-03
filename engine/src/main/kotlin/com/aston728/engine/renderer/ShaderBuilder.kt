package com.aston728.engine.renderer

import com.aston728.engine.types.ErrorHandler

internal interface ShaderBuilder {
    fun build(spec: ShaderSpec, errorCallback: ErrorHandler): Shader?
}
