package com.aston728.engine.internals.renderer

import com.aston728.engine.renderer.BlankShader
import com.aston728.engine.renderer.Shader
import com.aston728.engine.renderer.ShaderBuilder
import com.aston728.engine.renderer.ShaderSpec
import com.aston728.engine.types.ErrorHandler

class VulkanShaderBuilder : ShaderBuilder {
    override fun build(spec: ShaderSpec, errorCallback: ErrorHandler): Shader = BlankShader(spec.getName())
}
