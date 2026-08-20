package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.rendererBase.Renderer
import com.aston728.engine.core.types.DrawSequence
import com.aston728.engine.core.utils.Color
import org.lwjgl.opengl.GL33C.*

internal class OpenGLRenderer : Renderer() {
    override fun onClearWith(color: Color): Unit {
        val normalizedColor: FloatArray = color.toNormalizedFloatArray()
        glClearColor(normalizedColor[0], normalizedColor[1], normalizedColor[2], normalizedColor[3])
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }
    override fun onDraw(sequence: DrawSequence): Unit {
    }
}
