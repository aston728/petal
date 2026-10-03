package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.rendererBase.*
import org.lwjgl.opengl.GL11C.*

internal class OpenGLRenderManager : RenderManager() {
    override val api: GraphicsApi = GraphicsApi.OPEN_GL

    private val contexts: MutableList<OpenGLContext> = mutableListOf()
    private var currentContext: OpenGLContext = OpenGLContext()
    private val samplerManager: OpenGLShaderSamplerManager = OpenGLShaderSamplerManager()

    override fun onAddWindow(handle: Long): Unit {
        this.contexts.add(OpenGLContext.create(
            handle,
            this._isDebugOn, debugMessageCallback = { message -> this._logger.error("OPENGL (DEBUG)", message) }
        ))
    }
    override fun onRemoveWindow(handle: Long): Unit {
        val context: OpenGLContext = this.contexts.first { it.getHandle() == handle }
        context.free()
        this.contexts.remove(context)
    }
    override fun onResize(windowHandle: Long, size: IntSize): Unit {
        this.contexts.first { it.getHandle() == windowHandle }.resize(size)
    }
    override fun onBuildShader(spec: ShaderSpec): Shader? {
        val shader: Shader? = buildShader(
            spec, this.samplerManager,
            contextProvider = { this.currentContext },
            errorCallback = { message -> this._logger.error("OPENGL SHADER", message) }
        )
        shader?.addOnDestroyHandler({ shader ->
            this.contexts.forEach { it.destroyShaderState(shader) }
        })
        return shader
    }

    override fun onRender(windowHandle: Long, commands: List<RenderCommand>): Unit {
        this.currentContext = this.contexts.first { it.getHandle() == windowHandle }

        this.currentContext.makeCurrent()
        for (command in commands) {
            when (command) {
                is ClearCommand -> {
                    val normalizedColor: FloatArray = command.color.toNormalizedArray()
                    glClearColor(normalizedColor[0], normalizedColor[1], normalizedColor[2], normalizedColor[3])
                    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                }
                is BindShaderCommand -> { this.bindShader(command.shader) }
                is DrawCommand -> {
                    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
                }
                is MultiDrawCommand -> {
                    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0) // TODO
                }
            }
        }
        this.currentContext.swapBuffers()
    }

    override fun onFree(): Unit {
        this.samplerManager.free()
    }
}
