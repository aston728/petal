package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.Color

public sealed interface RenderCommand
public data class ClearCommand(public val color: Color) : RenderCommand
public data class BindCommand(public val shader: Shader, public val mesh: Mesh, public val material: Material) : RenderCommand
public data class DrawCommand(public val instanceI: Int) : RenderCommand
public data class MultiDrawCommand(public val instanceIndexes: List<Int>) : RenderCommand
