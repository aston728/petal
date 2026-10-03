package com.aston728.engine.openGLRenderer

import org.lwjgl.opengl.GL33C.glDeleteVertexArrays
import org.lwjgl.opengl.GL33C.glGenVertexArrays

internal class OpenGLShaderState {
    val vao: Int = glGenVertexArrays()

    internal fun destroy(): Unit {
        glDeleteVertexArrays(this.vao)
    }
}
