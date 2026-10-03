package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.Color

public sealed interface RenderCommand
public data class ClearCommand(public val color: Color) : RenderCommand
public data class BindShaderCommand(public val shader: Shader) : RenderCommand
public data class DrawCommand(public val instanceI: Int) : RenderCommand
public data class MultiDrawCommand(public val instanceIndexes: List<Int>) : RenderCommand
