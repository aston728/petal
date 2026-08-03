package com.aston728.engine.renderer

import com.aston728.engine.types.ErrorHandler

class BlankShaderBuilder : ShaderBuilder {
    override fun build(spec: ShaderSpec, errorCallback: ErrorHandler): Shader = BlankShader(spec.getName())
}
