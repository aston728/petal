package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.rendererBase.Shader

internal class VulkanShader(name: String, errorCallback: (String) -> Unit) : Shader(name, errorCallback) {
    override fun onFree(): Unit {}
    override fun exists(): Boolean = false
    override fun onBind(): Unit {}
}
