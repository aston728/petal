package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.rendererBase.*

internal class VulkanRenderManager : RenderManager() {
    override val api: GraphicsApi = GraphicsApi.VULKAN

    override fun getVersion(): String = ""
    override fun onFree(): Unit {}
    override fun onAddWindow(handle: Long): Unit {}
    override fun onRemoveWindow(handle: Long): Unit {}
    override fun onResize(windowHandle: Long, size: IntSize): Unit {}
    override fun onBuildShader(spec: ShaderSpec, errorCallback: (String) -> Unit): Shader = VulkanShader(spec.getName(), errorCallback)
    override fun onBuildMesh(spec: ShaderSpec, errorCallback: (String) -> Unit): Mesh = VulkanMesh(spec.getName(), spec.getVertexAttributes(), spec.getInstanceAttributes(), errorCallback)
    override fun onBuildMaterial(spec: ShaderSpec, errorCallback: (String) -> Unit): Material = VulkanMaterial(spec.getName(), spec.getUniforms(), errorCallback)
    override fun onRender(windowHandle: Long, commands: List<RenderCommand>): Unit {}
}
