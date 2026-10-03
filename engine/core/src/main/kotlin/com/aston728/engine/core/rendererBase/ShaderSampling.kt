package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.internals.Image

public data class ShaderSampledImage2D(public val img: Image, public val sampler: ShaderSampler = SHADER_DEFAULT_SAMPLER)
public data class ShaderSampledImage2DArray(public val imgs: List<Image>, public val sampler: ShaderSampler = SHADER_DEFAULT_SAMPLER)
