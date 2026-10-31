package com.aston728.engine.core.blankRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.rendererBase.*

internal class BlankRenderManager : RenderManager() {
    override val api: GraphicsApi = GraphicsApi.NONE

    override fun getVersion(): String = ""
    override fun onFree(): Unit {}
    override fun onAddWindow(handle: Long): Unit {}
    override fun onRemoveWindow(handle: Long): Unit {}
    override fun onResize(windowHandle: Long, size: IntSize): Unit {}
    override fun onBuildShader(spec: ShaderSpec, errorCallback: (String) -> Unit): Shader = BlankShader(spec.getName(), errorCallback)
    override fun onBuildMesh(spec: ShaderSpec, errorCallback: (String) -> Unit): Mesh = BlankMesh(spec.getName(), spec.getVertexAttributes(), spec.getInstanceAttributes(), errorCallback)
    override fun onBuildMaterial(spec: ShaderSpec, errorCallback: (String) -> Unit): Material = BlankMaterial(spec.getName(), spec.getUniforms(), errorCallback)

    override fun onRender(windowHandle: Long, commands: List<RenderCommand>): Unit {}
}
