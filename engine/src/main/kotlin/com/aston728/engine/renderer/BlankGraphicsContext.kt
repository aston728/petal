package com.aston728.engine.renderer

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.ErrorHandler

internal class BlankGraphicsContext : GraphicsContext {
    override fun getHandle(): Long = 0
    override fun onResize(size: IntSize): Unit {}
    override fun makeCurrent(): Unit {}
    override fun swapBuffers(): Unit {}
}
