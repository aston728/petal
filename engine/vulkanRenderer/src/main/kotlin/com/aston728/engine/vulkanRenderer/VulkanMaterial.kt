package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.rendererBase.Material
import com.aston728.engine.core.rendererBase.Shader
import com.aston728.engine.core.rendererBase.ShaderUniform
import com.aston728.engine.core.rendererBase.ShaderUniformInfo

internal class VulkanMaterial(
    name: String,
    uniformsInfo: List<ShaderUniformInfo<*>>,
    errorCallback: (String) -> Unit
) : Material(name, uniformsInfo, errorCallback) {
    override fun onAssignShader(shader: Shader): Unit {}
    override fun onBind(): Unit {}
    override fun <T> updateUniform(info: ShaderUniform<T>) {}
    override fun onFree(): Unit {}
}
