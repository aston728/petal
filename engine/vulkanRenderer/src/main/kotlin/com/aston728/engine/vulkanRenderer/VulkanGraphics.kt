package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.rendererBase.*
import com.aston728.engine.core.types.ErrorHandler

internal class VulkanGraphics : Graphics() {
    override val api: GraphicsApi = GraphicsApi.VULKAN

    override val _contextProvider: (Long, Boolean, ErrorHandler) -> GraphicsContext = { handle, isDebugOn, debugMessageCallback -> VulkanContext() }
    override val _shaderBuilder: ShaderBuilder = VulkanShaderBuilder()
    override val _renderer: Renderer = VulkanRenderer()

    override fun onDestroy() {}
}
