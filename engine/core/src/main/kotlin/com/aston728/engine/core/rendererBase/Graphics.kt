package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.types.ErrorHandler

public abstract class Graphics {
    public abstract val api: GraphicsApi

    protected abstract val _contextProvider: (Long, Boolean, ErrorHandler) -> GraphicsContext
    protected abstract val _shaderBuilder: ShaderBuilder
    protected abstract val _renderer: Renderer

    internal fun getContextProvider(): (Long, Boolean, ErrorHandler) -> GraphicsContext = this._contextProvider
    internal fun getShaderBuilder(): ShaderBuilder = this._shaderBuilder
    internal fun getRenderer(): Renderer = this._renderer
}
