package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.rendererBase.GraphicsApi
import com.aston728.engine.core.rendererBase.RenderManager
import com.aston728.engine.core.rendererBase.Shader
import com.aston728.engine.core.rendererBase.ShaderSpec

internal class OpenGLRenderManager : RenderManager() {
    override val api: GraphicsApi = GraphicsApi.OPEN_GL

    private val contexts: MutableList<OpenGLContext> = mutableListOf()
    private val shaderBuilder: OpenGLShaderBuilder = OpenGLShaderBuilder()
    private val samplerManager: OpenGLShaderSamplerManager = OpenGLShaderSamplerManager()
    private val renderer: OpenGLRenderer = OpenGLRenderer()

    override fun onAddWindow(handle: Long): Unit {
        this.contexts.add(OpenGLContext(
            handle,
            this._isDebugOn, debugMessageCallback = { message -> this._logger.error("OPENGL (DEBUG)", message) }
        ))
    }
    override fun onRemoveWindow(handle: Long): Unit {
        this.contexts.removeIf { it.getHandle() == handle }
    }
    override fun onResize(windowHandle: Long, size: IntSize): Unit {
        this.contexts.forEach {
            if (it.getHandle() == windowHandle) { it.resize(size) }
        }
    }
    override fun onBuildShader(windowHandle: Long, spec: ShaderSpec): Shader? {
        this.contexts.forEach {
            if (it.getHandle() == windowHandle) { it.makeCurrent() }
        }
        return this.shaderBuilder.build(
            spec, this.samplerManager,
            errorCallback = { message -> this._logger.error("OPENGL SHADER", message) }
        )
    }
    override fun onRender(windowHandle: Long, shaders: List<Shader>): Unit {
        val context: OpenGLContext = this.contexts.first { it.getHandle() == windowHandle }
        context.makeCurrent()
        this.renderer.draw(emptyList())
        @Suppress("unchecked_cast")
        (shaders as List<OpenGLShader>).forEach { it.draw(context) }
        context.swapBuffers()
    }
    override fun onFree(): Unit {
        this.samplerManager.free()
    }
}
