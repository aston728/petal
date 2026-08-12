package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.rendererBase.GraphicsContext

internal class VulkanContext : GraphicsContext() {
    override fun onGetHandle(): Long = 0
    override fun onResize(size: IntSize): Unit {}
    override fun onMakeCurrent(): Unit {}
    override fun onSwapBuffers(): Unit {}
}
