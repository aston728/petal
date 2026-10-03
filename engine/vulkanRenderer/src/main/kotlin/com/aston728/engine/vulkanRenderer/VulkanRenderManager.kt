package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.rendererBase.*

internal class VulkanRenderManager : RenderManager() {
    override val api: GraphicsApi = GraphicsApi.VULKAN

    override fun onAddWindow(handle: Long): Unit {}
    override fun onRemoveWindow(handle: Long): Unit {}
    override fun onResize(windowHandle: Long, size: IntSize): Unit {}
    override fun onBuildShader(spec: ShaderSpec): Shader = VulkanShader(spec.getName())
    override fun onRender(windowHandle: Long, commands: List<RenderCommand>): Unit {}
    override fun onFree(): Unit {}
}
