package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.Color

public enum class ShaderSamplerFilter { NEAREST, LINEAR }
public enum class ShaderSamplerMipmapMode { NONE, NEAREST, LINEAR }
public enum class ShaderSamplerAddressMode { EDGE_CLAMP, BORDER_CLAMP, REPEAT, REPEAT_MIRRORED }
public data class ShaderSampler(
    public val minFilter: ShaderSamplerFilter = ShaderSamplerFilter.NEAREST,
    public val magFilter: ShaderSamplerFilter = ShaderSamplerFilter.NEAREST,
    public val mipmapMode: ShaderSamplerMipmapMode = ShaderSamplerMipmapMode.NONE,
    public val addressModeX: ShaderSamplerAddressMode = ShaderSamplerAddressMode.EDGE_CLAMP,
    public val addressModeY: ShaderSamplerAddressMode = ShaderSamplerAddressMode.EDGE_CLAMP,
    public val borderColor: Color = Color(0u, 0u, 0u, 0u),
    public val minLOD: Float = 0.0f,
    public val maxLOD: Float = 1000.0f,
)

public val SHADER_DEFAULT_SAMPLER: ShaderSampler = ShaderSampler()
