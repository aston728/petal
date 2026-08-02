package com.aston728.engine.renderer

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.ErrorHandler

internal interface GraphicsContext {
    fun getHandle(): Long
    fun onResize(size: IntSize): Unit
    fun makeCurrent(): Unit
    fun swapBuffers(): Unit
}
