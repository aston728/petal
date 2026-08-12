package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.rendererBase.Shader
import com.aston728.engine.core.rendererBase.ShaderBuilder
import com.aston728.engine.core.rendererBase.ShaderSpec
import com.aston728.engine.core.types.ErrorHandler

internal class VulkanShaderBuilder : ShaderBuilder() {
    override fun onBuild(spec: ShaderSpec, errorCallback: ErrorHandler): Shader = VulkanShader(spec.getName())
}
