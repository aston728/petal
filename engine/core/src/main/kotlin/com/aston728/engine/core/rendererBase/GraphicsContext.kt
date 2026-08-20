package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.geometry.IntSize

public abstract class GraphicsContext {
    protected abstract fun onGetHandle(): Long
    protected abstract fun onMakeCurrent(): Unit
    protected abstract fun onResize(size: IntSize): Unit
    protected abstract fun onSwapBuffers(): Unit

    internal fun getHandle(): Long = this.onGetHandle()
    internal fun resize(size: IntSize): Unit = this.onResize(size)
    internal fun makeCurrent(): Unit = this.onMakeCurrent()
    public abstract fun bindTexture(unit: Int, texture: Int, textureType: Int, sampler: Int): Unit // TODO
    internal fun swapBuffers(): Unit = this.onSwapBuffers()
}
