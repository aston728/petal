package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.blankRenderer.BlankShader
import com.aston728.engine.core.utils.NamedObject

public class ShaderUniform<T>(public val handle: ShaderUniformHandle<T>, public var value: T) {
    public fun set(value: Any) {
        this.value = value as T // TODO
    }
}

public abstract class Material(
    name: String,
    protected val _uniforms: List<ShaderUniformInfo<*>>,
    protected val _errorCallback: (String) -> Unit
) : NamedObject<Material>(name) {
    private var assignedShader: Shader = BlankShader("Element Shader", errorCallback = {})
    private val uniforms: List<ShaderUniform<*>> = this._uniforms.map { this.createUniform(it) }
    private val uniformsUpdateQueue: MutableList<ShaderUniform<*>> = mutableListOf()

    protected abstract fun onAssignShader(shader: Shader): Unit
    protected abstract fun onBind(): Unit
    protected abstract fun <T> updateUniform(info: ShaderUniform<T>): Unit
    protected abstract fun onFree(): Unit

    // TODO
    private fun <T> createUniform(info: ShaderUniformInfo<T>): ShaderUniform<T> = ShaderUniform(info.handle, info.defaultValue)

    internal fun assignShader(shader: Shader): Unit {
        if (this.assignedShader != shader) {
            shader.bind()
            this.onAssignShader(shader)
            this.uniforms.forEach { this.updateUniform(it) }
            this.uniformsUpdateQueue.clear()
            this.assignedShader = shader
        }
    }
    internal fun bind(): Unit {
        this.onBind()
        this.uniformsUpdateQueue.forEach { this.updateUniform(it) }
    }
    public fun <T : Any> setUniform(handle: ShaderUniformHandle<T>, value: T): Unit {
        this.uniforms.first { it.handle == handle }.set(value)
        this.uniformsUpdateQueue.add(ShaderUniform(handle, value)) // TODO
    }
    internal fun free(): Unit = this.onFree()
}
