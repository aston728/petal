package com.aston728.engine.core.rendererBase

internal class ShaderManager(
    private val buildShader: (ShaderSpec) -> Shader? = { spec -> BlankShader(spec.getName()) },
    private val errorCallback: (String) -> Unit = { message -> System.err.println("[SHADER] $message") }
) {
    private val shaderCache: MutableMap<String, Shader> = mutableMapOf()

    internal fun acquire(spec: ShaderSpec): ShaderInstanceHandle? {
        val specError: String? = spec.checkValidity()
        if (specError != null) {
            this.errorCallback(specError)
            return null
        }

        val shader: Shader = this.shaderCache.getOrPut(spec.getRepresentation()) {
            val shader: Shader = this.buildShader(spec) ?: return null
            spec.getUniformsUnsafe().forEach { it.applyDefault(shader) }
            shader
        }
        val instanceI: Int = shader.addInstance()
        return ShaderInstanceHandle(shader, instanceI, this.errorCallback)
    }
    internal fun release(handle: ShaderInstanceHandle): Unit {
        handle.shader.removeInstance(handle.instanceI)
        handle.invalidate()
        if (handle.shader.hasInstances()) {
            handle.shader.destroy()
            this.shaderCache.entries.removeIf { it.value == handle.shader }
        }
    }
    internal fun releaseAll(): Unit {
        this.shaderCache.values.forEach { it.destroy() }
        this.shaderCache.clear()
    }
}
