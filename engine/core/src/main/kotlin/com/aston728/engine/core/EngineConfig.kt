package com.aston728.engine.core

import com.aston728.engine.core.rendererBase.GraphicsApi

public data class EngineConfig(
    public val engineMode: EngineMode = EngineMode.DEVELOPMENT,
    public val graphicsApi: GraphicsApi = GraphicsApi.OPEN_GL,
)
