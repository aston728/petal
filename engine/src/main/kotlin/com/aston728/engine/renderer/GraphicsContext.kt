package com.aston728.engine.renderer

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.ErrorHandler

internal abstract class GraphicsContext {
    protected abstract fun enableDebugging(messageCallback: ErrorHandler): Unit
    internal abstract fun onResize(size: IntSize): Unit
    internal abstract fun startDrawing(): Unit
    internal abstract fun stopDrawing(): Unit
}
