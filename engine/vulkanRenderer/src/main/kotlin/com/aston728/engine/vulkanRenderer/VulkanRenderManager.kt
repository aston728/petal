package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.rendererBase.GraphicsApi
import com.aston728.engine.core.rendererBase.RenderManager
import com.aston728.engine.core.rendererBase.Shader
import com.aston728.engine.core.rendererBase.ShaderSpec

internal class VulkanRenderManager : RenderManager() {
    override val api: GraphicsApi = GraphicsApi.VULKAN

    override fun onAddWindow(handle: Long): Unit {}
    override fun onRemoveWindow(handle: Long): Unit {}
    override fun onResize(windowHandle: Long, size: IntSize): Unit {}
    override fun onBuildShader(windowHandle: Long, spec: ShaderSpec): Shader = VulkanShader(spec.getName())
    override fun onRender(windowHandle: Long, shaders: List<Shader>): Unit {}
    override fun onFree(): Unit {}
}
