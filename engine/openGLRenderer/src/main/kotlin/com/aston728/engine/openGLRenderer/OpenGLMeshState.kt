package com.aston728.engine.openGLRenderer

import org.lwjgl.opengl.GL33C.glDeleteVertexArrays
import org.lwjgl.opengl.GL33C.glGenVertexArrays

internal class OpenGLMeshState {
    val vao: Int = glGenVertexArrays()

    internal fun free(): Unit {
        glDeleteVertexArrays(this.vao)
    }
}
