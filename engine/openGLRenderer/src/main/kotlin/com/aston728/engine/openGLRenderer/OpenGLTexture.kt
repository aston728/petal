package com.aston728.engine.openGLRenderer

import org.lwjgl.opengl.GL11C.glDeleteTextures

internal class OpenGLTexture(internal val value: Int) {
    internal val updateQueue: MutableList<(OpenGLTexture) -> Unit> = mutableListOf()
    internal val onFreeHandlers: MutableList<(OpenGLTexture) -> Unit> = mutableListOf()

    internal fun free(): Unit {
        glDeleteTextures(value)
        this.onFreeHandlers.forEach { it(this) }
    }
}
