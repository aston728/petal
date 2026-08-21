package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.geometry.IntSize

internal class BlankRenderManager : RenderManager() {
    override val api: GraphicsApi = GraphicsApi.NONE

    override fun onAddWindow(handle: Long): Unit {}
    override fun onRemoveWindow(handle: Long): Unit {}
    override fun onResize(windowHandle: Long, size: IntSize): Unit {}
    override fun onBuildShader(windowHandle: Long, spec: ShaderSpec): Shader = BlankShader(spec.getName())
    override fun onRender(windowHandle: Long, shaders: List<Shader>): Unit {}
    override fun onFree(): Unit {}
}
