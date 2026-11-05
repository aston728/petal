package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.rendererBase.*
import org.lwjgl.opengl.GL31C.*

internal class OpenGLRenderManager : RenderManager() {
    override val api: GraphicsApi = GraphicsApi.OPEN_GL

    private val contexts: MutableList<OpenGLContext> = mutableListOf()
    private var currentContext: OpenGLContext = OpenGLContext()
    private val samplerManager: OpenGLShaderSamplerManager = OpenGLShaderSamplerManager()

    private val shaderMappings: MutableMap<Shader, Int> = mutableMapOf() // TODO

    override fun getVersion(): String {
        if (this.contexts.isEmpty()) {
            this._logger.warn("OPENGL", "Cannot obtain the openGL version, no window exists")
            return "Unknown"
        }
        return glGetString(GL_VERSION) ?: "Unknown"
    }

    override fun onFree(): Unit {
        this.samplerManager.free()
    }

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

    override fun onBuildShader(spec: ShaderSpec, errorCallback: (String) -> Unit): Shader? {
        val shader: OpenGLShader? = buildShader(spec, errorCallback)
        if (shader != null) { this.shaderMappings[shader] = shader.program }
        return shader
    }
    override fun onBuildMesh(spec: ShaderSpec, errorCallback: (String) -> Unit): Mesh {
        val mesh: Mesh = OpenGLMesh(
            spec.getName(),
            spec.getVertexAttributes(), spec.getInstanceAttributes(),
            contextProvider = { this.currentContext },
            stateProvider  = { context, mesh -> context.getOrPutMeshState(mesh) { createMeshState(mesh) } },
            errorCallback = errorCallback
        )
        mesh.addOnDestroyHandler({ mesh ->
            this.contexts.forEach { it.freeMeshState(mesh) }
        })
        return mesh
    }

    override fun onBuildMaterial(spec: ShaderSpec, errorCallback: (String) -> Unit): Material = OpenGLMaterial(
        spec.getName(),
        spec.getUniforms(), this.samplerManager,
        errorCallback,
        contextProvider = { this.currentContext }, contextsProvider = { this.contexts },
        programProvider = { shader -> this.shaderMappings.getOrDefault(shader, OpenGLShader.INVALID_PROGRAM) }
    )

    override fun onRender(windowHandle: Long, commands: List<RenderCommand>): Unit {
        this.currentContext = this.contexts.first { it.getHandle() == windowHandle }

        this.currentContext.makeCurrent()
        var currentMesh: OpenGLMesh = OpenGLMesh("", emptyList(), emptyList(), {}, { this.currentContext }, { a, b-> OpenGLMeshState ()})
        for (command in commands) {
            when (command) {
                is ClearCommand -> {
                    val normalizedColor: FloatArray = command.color.toNormalizedArray()
                    glClearColor(normalizedColor[0], normalizedColor[1], normalizedColor[2], normalizedColor[3])
                    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                }
                is BindCommand -> {
                    this.bindShader(command.shader)
                    this.bindMesh(command.mesh)
                    this.bindMaterial(command.material)
                    currentMesh = command.mesh as OpenGLMesh
                }
                is DrawCommand -> {
                    // TODO
                    currentMesh.getInstanceData().position(currentMesh.getInstanceDataStrideSize() * command.instanceI)
                    glBindBuffer(GL_ARRAY_BUFFER, currentMesh.instanceBuffer)
                    glBufferData(GL_ARRAY_BUFFER, currentMesh.getInstanceData(), GL_STATIC_DRAW)
                    glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, 1)
                    currentMesh.getInstanceData().rewind()
                    glBindBuffer(GL_ARRAY_BUFFER, currentMesh.instanceBuffer)
                    glBufferData(GL_ARRAY_BUFFER, currentMesh.getInstanceData(), GL_STATIC_DRAW)
                }
                is MultiDrawCommand -> {
                    glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, command.instanceIndexes.size)
                }
            }
        }
        this.currentContext.swapBuffers()
    }
}
