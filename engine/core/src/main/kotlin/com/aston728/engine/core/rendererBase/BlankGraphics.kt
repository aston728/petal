package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.types.ErrorHandler

internal class BlankGraphics : Graphics() {
    override val api: GraphicsApi = GraphicsApi.NONE

    override val _contextProvider: (Long, Boolean, ErrorHandler) -> GraphicsContext = { handle, isDebugOn, debugMessageCallback ->
        BlankGraphicsContext()
    }
    override val _shaderBuilder: ShaderBuilder = BlankShaderBuilder()
    override val _renderer: Renderer = BlankRenderer()
}
