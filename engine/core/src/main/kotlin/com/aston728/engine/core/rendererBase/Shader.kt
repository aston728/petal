package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.NamedObject

public abstract class Shader(
    name: String,
    protected val _errorCallback: (String) -> Unit
) : NamedObject<Shader>(name) {
    protected abstract fun onFree(): Unit
    public abstract fun exists(): Boolean
    protected abstract fun onBind(): Unit

    internal fun free(): Unit = this.onFree()
    internal fun bind(): Unit = this.onBind()
}
