package com.aston728.engine.core

import com.aston728.engine.core.rendererBase.GraphicsApi

public enum class EngineMode { DEVELOPMENT, RELEASE, DEBUG }

public data class EngineConfig(
    public val engineMode: EngineMode = EngineMode.RELEASE,
    public val graphicsApi: GraphicsApi = GraphicsApi.OPEN_GL,
)

public val DEFAULT_ENGINE_CONFIG: EngineConfig = EngineConfig()
