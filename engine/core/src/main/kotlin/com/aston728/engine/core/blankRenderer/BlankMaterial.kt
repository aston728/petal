package com.aston728.engine.core.blankRenderer

import com.aston728.engine.core.rendererBase.Material
import com.aston728.engine.core.rendererBase.Shader
import com.aston728.engine.core.rendererBase.ShaderUniform
import com.aston728.engine.core.rendererBase.ShaderUniformInfo

internal class BlankMaterial(
    name: String,
    uniforms: List<ShaderUniformInfo<*>>,
    errorCallback: (String) -> Unit
) : Material(name, uniforms, errorCallback) {
    override fun onBind(): Unit {}
    override fun onAssignShader(shader: Shader): Unit {}
    override fun <T> updateUniform(info: ShaderUniform<T>) {}
    override fun onFree(): Unit {}
}
