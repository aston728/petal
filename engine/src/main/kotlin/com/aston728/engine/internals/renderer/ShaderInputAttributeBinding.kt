package com.aston728.engine.internals.renderer

import com.aston728.engine.renderer.ShaderInstanceAttributeHandle
import com.aston728.engine.renderer.ShaderVertexAttributeHandle

internal class ShaderVertexAttributeBinding(
    internal val handle: ShaderVertexAttributeHandle<*>, internal val name: String,
    internal val location: Int, internal val bufferLocation: Int,
)
internal class ShaderInstanceAttributeBinding(
    internal val handle: ShaderInstanceAttributeHandle<*>, internal val name: String,
    internal val location: Int, internal val bufferLocation: Int,
)
