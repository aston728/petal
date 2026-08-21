package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.geometry.IntPosition
import com.aston728.engine.core.internals.image.Image
import com.aston728.engine.core.utils.Color
import org.lwjgl.opengl.GL33C.*

internal class OpenGLRenderer {
    internal fun clearWith(color: Color): Unit {
        val normalizedColor: FloatArray = color.toNormalizedFloatArray()
        glClearColor(normalizedColor[0], normalizedColor[1], normalizedColor[2], normalizedColor[3])
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }
    internal fun draw(sequence: List<Pair<Image, IntPosition>>): Unit {}
}
