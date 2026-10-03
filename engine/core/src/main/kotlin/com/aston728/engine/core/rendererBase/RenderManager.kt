package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.FrozenLogger
import com.aston728.engine.core.Window
import com.aston728.engine.core.defaultEngineContext
import com.aston728.engine.core.geometry.IntSize

public enum class GraphicsApi { NONE, OPEN_GL, VULKAN }

public abstract class RenderManager {
    public abstract val api: GraphicsApi
    protected var _logger: FrozenLogger = defaultEngineContext.logger
        private set
    protected var _isDebugOn: Boolean = defaultEngineContext.isDebugOn
        private set

    protected abstract fun onAddWindow(handle: Long): Unit
    protected abstract fun onRemoveWindow(handle: Long): Unit
    protected abstract fun onResize(windowHandle: Long, size: IntSize): Unit
    protected abstract fun onBuildShader(spec: ShaderSpec): Shader?
    protected abstract fun onRender(windowHandle: Long, commands: List<RenderCommand>): Unit
    protected abstract fun onFree(): Unit

    internal fun setLogger(logger: FrozenLogger): Unit {
        this._logger = logger
    }
    internal fun setIsDebugOn(isDebugOn: Boolean): Unit {
        this._isDebugOn = isDebugOn
    }

    internal fun addWindow(window: Window): Unit = this.onAddWindow(window.getInternalWindow().getHandle())
    internal fun removeWindow(window: Window): Unit = this.onRemoveWindow(window.getInternalWindow().getHandle())
    internal fun resize(window: Window, size: IntSize): Unit = this.onResize(window.getInternalWindow().getHandle(), size)
    internal fun buildShader(spec: ShaderSpec): Shader? = this.onBuildShader(spec)
    protected fun bindShader(shader: Shader): Unit = shader.bind()
    internal fun render(window: Window, commands: List<RenderCommand>): Unit = this.onRender(window.getInternalWindow().getHandle(), commands)
    internal fun free(): Unit = this.onFree()
}
