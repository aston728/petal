package com.aston728.engine.renderer

import com.aston728.engine.utils.Color

import com.aston728.engine.types.DrawSequence

internal abstract class Renderer {
    internal abstract fun clearWith(color: Color): Unit
    internal abstract fun draw(sequence: DrawSequence): Unit
}
