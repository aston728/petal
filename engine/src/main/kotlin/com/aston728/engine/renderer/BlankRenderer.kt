package com.aston728.engine.renderer

import com.aston728.engine.utils.Color

import com.aston728.engine.types.DrawSequence

internal class BlankRenderer : Renderer {
    override fun clearWith(color: Color): Unit {}
    override fun draw(sequence: DrawSequence): Unit {}
}
