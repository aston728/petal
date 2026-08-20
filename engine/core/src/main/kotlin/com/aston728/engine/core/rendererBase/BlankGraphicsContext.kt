package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.geometry.IntSize

internal class BlankGraphicsContext : GraphicsContext() {
    override fun onGetHandle(): Long = 0
    override fun onMakeCurrent(): Unit {}
    override fun onResize(size: IntSize): Unit {}
    override fun bindTexture(unit: Int, texture: Int, textureType: Int, sampler: Int) {}
    override fun onSwapBuffers(): Unit {}
}
