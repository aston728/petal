package com.aston728.engine.core

import com.aston728.engine.renderer.GraphicsApi

data class EngineConfig(
    val engineMode: EngineMode = EngineMode.DEVELOPMENT,
    val graphicsApi: GraphicsApi = GraphicsApi.OPEN_GL,
)
