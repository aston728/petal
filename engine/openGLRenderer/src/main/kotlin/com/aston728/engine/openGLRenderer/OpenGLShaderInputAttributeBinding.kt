package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.rendererBase.ShaderInstanceAttributeHandle
import com.aston728.engine.core.rendererBase.ShaderVertexAttributeHandle

internal class OpenGLShaderVertexAttributeBinding(
    internal val handle: ShaderVertexAttributeHandle<*>, internal val name: String,
    internal val location: Int, internal val bufferLocation: Int,
)
internal class OpenGLShaderInstanceAttributeBinding(
    internal val handle: ShaderInstanceAttributeHandle<*>, internal val name: String,
    internal val location: Int, internal val bufferLocation: Int,
)
