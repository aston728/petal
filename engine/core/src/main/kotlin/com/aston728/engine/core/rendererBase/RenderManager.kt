package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.FrozenLogger
import com.aston728.engine.core.Window
import com.aston728.engine.core.defaultEngineContext
import com.aston728.engine.core.geometry.IntSize

public abstract class RenderManager {
    public abstract val api: GraphicsApi
    protected var _logger: FrozenLogger = defaultEngineContext.logger
    protected var _isDebugOn: Boolean = defaultEngineContext.isDebugOn

    protected abstract fun onAddWindow(handle: Long): Unit
    protected abstract fun onRemoveWindow(handle: Long): Unit
    protected abstract fun onResize(windowHandle: Long, size: IntSize): Unit
    protected abstract fun onBuildShader(windowHandle: Long, spec: ShaderSpec): Shader?
    protected abstract fun onRender(windowHandle: Long, shaders: List<Shader>): Unit
    protected abstract fun onFree(): Unit

    internal fun setLogger(logger: FrozenLogger): RenderManager = apply {
        this._logger = logger
    }
    internal fun setIsDebugOn(isDebugOn: Boolean): RenderManager = apply {
        this._isDebugOn = isDebugOn
    }

    internal fun addWindow(window: Window): Unit = this.onAddWindow(window.getInternalWindow().getHandle())
    internal fun removeWindow(window: Window): Unit = this.onRemoveWindow(window.getInternalWindow().getHandle())
    internal fun resize(window: Window, size: IntSize): Unit = this.onResize(window.getInternalWindow().getHandle(), size)
    internal fun buildShader(windowHandle: Long, spec: ShaderSpec): Shader? = this.onBuildShader(windowHandle, spec)
    internal fun render(window: Window, shaders: List<Shader>): Unit = this.onRender(window.getInternalWindow().getHandle(), shaders)
    internal fun free(): Unit = this.onFree()
}
