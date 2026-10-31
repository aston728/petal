package com.aston728.engine.core.blankRenderer

import com.aston728.engine.core.rendererBase.Shader

internal class BlankShader(name: String, errorCallback: (String) -> Unit) : Shader(name, errorCallback) {
    override fun onFree(): Unit {}
    override fun exists(): Boolean = false
    override fun onBind(): Unit {}
}
