package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.rendererBase.Graphics
import com.aston728.engine.core.rendererBase.GraphicsApi
import com.aston728.engine.core.rendererBase.GraphicsContext
import com.aston728.engine.core.rendererBase.Renderer
import com.aston728.engine.core.types.ErrorHandler

internal class OpenGLGraphics : Graphics() {
    override val api: GraphicsApi = GraphicsApi.OPEN_GL

    override val _contextProvider: (Long, Boolean, ErrorHandler) -> GraphicsContext = { handle, isDebugOn, debugMessageCallback ->
        OpenGLContext(handle, isDebugOn, debugMessageCallback)
    }
    override val _shaderBuilder: OpenGLShaderBuilder = OpenGLShaderBuilder()
    override val _renderer: Renderer = OpenGLRenderer()

    override fun onDestroy() {
        this._shaderBuilder.destroy()
    }
}
