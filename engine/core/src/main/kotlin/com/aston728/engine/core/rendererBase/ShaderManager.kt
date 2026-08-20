package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.types.ErrorHandler

private data class ShaderRepresentation(private val context: GraphicsContext, val spec: ShaderSpec)

internal class ShaderManager(
    private val builder: ShaderBuilder = BlankShaderBuilder(),
    private val errorCallback: ErrorHandler = { message -> System.err.println("[SHADER] $message") }
) : ShaderProvider {
    private val specCache: MutableMap<String, ShaderSpec> = mutableMapOf()
    private val shaderCache: MutableMap<ShaderRepresentation, Shader> = mutableMapOf()
    private val instanceHandles: MutableMap<Shader, MutableList<ShaderInstanceHandle>> = mutableMapOf()

    override fun specGetOrMerge(vararg specs: ShaderSpec): ShaderSpec {
        val spec: ShaderSpec = ShaderSpec.merge(*specs)
        return this.specCache.getOrPut(spec.getRepresentation()) { spec }
    }

    override fun acquire(context: GraphicsContext, spec: ShaderSpec): ShaderInstanceHandle? {
        val specError: String? = spec.checkValidity()
        if (specError != null) {
            this.errorCallback(specError)
            this.specCache.remove(spec.getRepresentation())
            return null
        }

        spec.freeze()
        val shader: Shader = this.shaderCache.getOrPut(ShaderRepresentation(context, spec)) {
            val shader: Shader? = this.builder.build(spec, this.errorCallback)
            if (shader == null) {
                this.specCache.remove(spec.getRepresentation())
                return null
            }

            spec.getUniformsUnsafe().forEach { it.applyDefault(shader) }
            shader
        }

        val handles: MutableList<ShaderInstanceHandle> = this.instanceHandles.getOrPut(shader) { mutableListOf() }
        val handle: ShaderInstanceHandle = ShaderInstanceHandle(shader, handles.size)
        shader.addInstance()
        handles.add(handle)

        return handle
    }
    override fun release(handle: ShaderInstanceHandle): Unit {
        val handles: MutableList<ShaderInstanceHandle> = this.instanceHandles.getOrDefault(handle.shader, mutableListOf())
        handles.remove(handle)
        if (handles.isEmpty()) {
            handle.shader.destroy()
            val representation: ShaderRepresentation? = this.shaderCache.entries.firstOrNull { it.value == handle.shader }?.key
            if (representation != null) {
                this.specCache.remove(representation.spec.getRepresentation())
                this.shaderCache.remove(representation)
            }
            this.instanceHandles.remove(handle.shader)
        } else {
            handle.shader.removeInstance(handle.instanceI)
            handles.subList(handle.instanceI, handles.size).forEach { it.instanceI-- }
        }
    }

    internal fun releaseAll(): Unit {
        this.shaderCache.values.forEach { it.destroy() }
        this.specCache.clear()
        this.shaderCache.clear()
        this.instanceHandles.clear()
    }
}
