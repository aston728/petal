package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.types.DrawSequence
import com.aston728.engine.core.utils.Color

public abstract class Renderer {
    protected abstract fun onClearWith(color: Color): Unit
    protected abstract fun onDraw(sequence: DrawSequence): Unit

    internal fun clearWith(color: Color): Unit = this.onClearWith(color)
    internal fun draw(sequence: DrawSequence): Unit = this.onDraw(sequence)
}
