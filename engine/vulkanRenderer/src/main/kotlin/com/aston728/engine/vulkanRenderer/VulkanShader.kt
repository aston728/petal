package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.rendererBase.Shader
import com.aston728.engine.core.rendererBase.ShaderInstanceAttributeHandle
import com.aston728.engine.core.rendererBase.ShaderUniformHandle
import com.aston728.engine.core.rendererBase.ShaderVertexAttributeHandle
import java.nio.ByteBuffer

internal class VulkanShader(name: String) : Shader(name) {
    override fun exists(): Boolean = false

    override fun computeHasInstances(): Boolean = false
    override fun onAddInstance(): Int = -1
    override fun onRemoveInstance(i: Int): Unit {}
    override fun <T> onSetVertexAttribute(
        handle: ShaderVertexAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        values: Array<T>
    ): Unit {}
    override fun <T> onSetInstanceAttribute(
        handle: ShaderInstanceAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        value: T
    ): Unit {}
    override fun <T> setUniform(handle: ShaderUniformHandle<T>, value: T): Unit {}

    override fun onBind(): Unit {}
    override fun onDestroy(): Unit {}
}
