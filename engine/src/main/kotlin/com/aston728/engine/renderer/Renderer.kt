package com.aston728.engine.renderer

import com.aston728.engine.utils.Color

import com.aston728.engine.types.DrawSequence

internal interface Renderer {
    fun clearWith(color: Color): Unit
    fun draw(sequence: DrawSequence): Unit
}
