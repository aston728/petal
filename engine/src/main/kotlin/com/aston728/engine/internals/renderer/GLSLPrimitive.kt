package com.aston728.engine.internals.renderer

import org.lwjgl.opengl.GL11C.GL_INT
import org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11C.GL_FLOAT

internal enum class GLSLPrimitive(private val glEnum: Int, private val byteSize: Int) {
    INT(GL_INT, 4), UINT(GL_UNSIGNED_INT, 4),
    FLOAT(GL_FLOAT, 4);

    internal fun getByteSize(): Int = this.byteSize
    internal fun toGLEnum(): Int = this.glEnum
}
