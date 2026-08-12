package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.types.ErrorHandler

internal class BlankShaderBuilder : ShaderBuilder() {
    override fun onBuild(spec: ShaderSpec, errorCallback: ErrorHandler): Shader = BlankShader(spec.getName())
}
