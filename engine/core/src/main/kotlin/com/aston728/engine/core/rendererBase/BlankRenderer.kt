package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.types.DrawSequence
import com.aston728.engine.core.utils.Color

internal class BlankRenderer : Renderer() {
    override fun onClearWith(color: Color): Unit {}
    override fun onDraw(sequence: DrawSequence): Unit {}
}
