package com.aston728.engine.renderer

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.ErrorHandler

internal class BlankGraphicsContext : GraphicsContext() {
    override fun enableDebugging(messageCallback: ErrorHandler): Unit {}
    override fun onResize(size: IntSize): Unit {}
    override fun startDrawing(): Unit {}
    override fun stopDrawing(): Unit {}
}
