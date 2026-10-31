package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.DEFAULT_IS_DEBUG_ON
import com.aston728.engine.core.DEFAULT_LOGGER
import com.aston728.engine.core.FrozenLogger
import com.aston728.engine.core.Window
import com.aston728.engine.core.geometry.IntSize

public enum class GraphicsApi { NONE, OPEN_GL, VULKAN }

public abstract class RenderManager {
    public abstract val api: GraphicsApi

    protected var _logger: FrozenLogger = DEFAULT_LOGGER
        private set
    protected var _isDebugOn: Boolean = DEFAULT_IS_DEBUG_ON
        private set

    internal fun setLogger(logger: FrozenLogger): Unit {
        this._logger = logger
    }
    internal fun setIsDebugOn(isDebugOn: Boolean): Unit {
        this._isDebugOn = isDebugOn
    }

    public abstract fun getVersion(): String
    protected abstract fun onFree(): Unit
    protected abstract fun onAddWindow(handle: Long): Unit
    protected abstract fun onRemoveWindow(handle: Long): Unit
    protected abstract fun onResize(windowHandle: Long, size: IntSize): Unit
    protected abstract fun onBuildShader(spec: ShaderSpec, errorCallback: (String) -> Unit): Shader?
    protected abstract fun onBuildMesh(spec: ShaderSpec, errorCallback: (String) -> Unit): Mesh
    protected abstract fun onBuildMaterial(spec: ShaderSpec, errorCallback: (String) -> Unit): Material
    protected abstract fun onRender(windowHandle: Long, commands: List<RenderCommand>): Unit

    internal fun free(): Unit = this.onFree()
    internal fun addWindow(window: Window): Unit = this.onAddWindow(window.getInternalWindow().getHandle())
    internal fun removeWindow(window: Window): Unit = this.onRemoveWindow(window.getInternalWindow().getHandle())
    internal fun resize(window: Window, size: IntSize): Unit = this.onResize(window.getInternalWindow().getHandle(), size)
    internal fun buildShader(spec: ShaderSpec, errorCallback: (String) -> Unit): Shader? = this.onBuildShader(spec, errorCallback)
    protected fun bindShader(shader: Shader): Unit = shader.bind()
    internal fun buildMesh(spec: ShaderSpec, errorCallback: (String) -> Unit): Mesh = this.onBuildMesh(spec, errorCallback)
    protected fun bindMesh(mesh: Mesh): Unit = mesh.bind()
    internal fun buildMaterial(spec: ShaderSpec, errorCallback: (String) -> Unit): Material = this.onBuildMaterial(spec, errorCallback)
    protected fun bindMaterial(material: Material): Unit = material.bind()
    internal fun render(window: Window, commands: List<RenderCommand>): Unit = this.onRender(window.getInternalWindow().getHandle(), commands)
}
