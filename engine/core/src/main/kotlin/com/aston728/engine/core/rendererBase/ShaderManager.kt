package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.FrozenLogger
import com.aston728.engine.core.blankRenderer.BlankMaterial
import com.aston728.engine.core.blankRenderer.BlankMesh
import com.aston728.engine.core.blankRenderer.BlankShader
import com.aston728.engine.core.elementBase.UIElement

internal class ShaderManager(
    private val logger: FrozenLogger,
    private val buildShader: (ShaderSpec, (String) -> Unit) -> Shader? = { spec, errorCallback -> BlankShader(spec.getName(), errorCallback) },
    private val buildMesh: (ShaderSpec, (String) -> Unit) -> Mesh = { spec, errorCallback -> BlankMesh(spec.getName(), spec.getVertexAttributes(), spec.getInstanceAttributes(), errorCallback) },
    private val buildMaterial: (ShaderSpec, (String) -> Unit) -> Material = { spec, errorCallback -> BlankMaterial(spec.getName(), spec.getUniforms(), errorCallback) }
) {
    private val shaderCache: MutableMap<String, Shader> = mutableMapOf()
    private val meshCache: MutableMap<String, Mesh> = mutableMapOf()
    private val materialCache: MutableMap<String, Material> = mutableMapOf()

    internal fun acquire(spec: ShaderSpec, element: UIElement<*>): ShaderInstanceHandle? {
        val specError: String? = spec.checkValidity()
        if (specError != null) {
            this.logger.error("SHADER", specError)
            return null
        }

        val shader: Shader = this.shaderCache.getOrPut(spec.getRepresentation()) {
            val shader: Shader = this.buildShader(spec) { message -> this.logger.error("SHADER", message) } ?: return null
            shader
        }
        val mesh: Mesh = this.meshCache.getOrPut(spec.getRepresentation()) {
            val mesh: Mesh = this.buildMesh(spec) { message -> this.logger.error("MESH", message) }
            mesh.getVertexAttributes().forEach { it.applyDefault(mesh) }
            mesh
        }
        val material: Material = this.materialCache.getOrPut(spec.getRepresentation()) {
            val material: Material = this.buildMaterial(spec) { message -> this.logger.error("MATERIAL", message) }
            material
        }
        mesh.allocate()
        shader.bind() // TODO
        material.assignShader(shader)
        val instanceI: Int = mesh.addInstance(element)
        return ShaderInstanceHandle(
            shader, mesh, material, instanceI,
            errorCallback = { message -> this.logger.error("MESH INSTANCE", message) }
        )
    }
    internal fun release(handle: ShaderInstanceHandle): Unit {
        handle.mesh.removeInstance(handle.instanceI)
        handle.invalidate()
        if (handle.mesh.hasInstances()) {
            handle.shader.free()
            handle.mesh.free()
            handle.material.free()
            this.shaderCache.entries.removeIf { it.value === handle.shader }
        }
    }
    internal fun releaseAll(): Unit {
        this.shaderCache.values.forEach { it.free() }
        this.meshCache.values.forEach { it.free() }
        this.materialCache.values.forEach { it.free()  }
        this.shaderCache.clear()
        this.meshCache.clear()
        this.materialCache.clear()
    }
}
