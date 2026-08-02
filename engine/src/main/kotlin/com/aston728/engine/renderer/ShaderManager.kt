package com.aston728.engine.renderer

import org.lwjgl.opengl.GL20C.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20C.glVertexAttribPointer
import org.lwjgl.opengl.GL30C.glVertexAttribIPointer

import com.aston728.engine.internals.renderer.GLSLAttributeType
import com.aston728.engine.internals.renderer.GLSLPrimitive
import com.aston728.engine.internals.renderer.OpenGLShader

import com.aston728.engine.types.ErrorHandler
import org.lwjgl.opengl.GL33C.glVertexAttribDivisor

class ShaderManager(private val errorCallback: ErrorHandler = { message -> System.err.println("[SHADER] $message") }) : ShaderProvider {
    private val cache: MutableMap<String, Shader> = mutableMapOf()
    private val instanceHandles: MutableMap<Shader, MutableList<ShaderInstanceHandle>> = mutableMapOf()

    override fun acquirePlaceholder(): ShaderInstanceHandle = ShaderInstanceHandle(BlankShader("Element Shader"), -1)

    private fun createVertexSource(spec: ShaderSpec, uniformsString: String): String {
        val vertexAttributesString: String = spec.getVertexAttributes().joinToString("\n") {
            "layout (location = ${it.location}) in ${it.handle.descriptor.type} ${it.name};"
        }
        val instanceAttributesString: String = spec.getInstanceAttributes().joinToString("\n") {
            "layout (location = ${it.location}) in ${it.handle.descriptor.type} ${it.name};"
        }
        val outputAttributesString: String = spec.getIntermediateAttributes().joinToString("\n") { "out ${it.type} ${it.name};" }

        return """
            #version 330 core
            $vertexAttributesString
            $instanceAttributesString
            $outputAttributesString
            $uniformsString
            void main() {
                ${spec.getVertexShaderBody()}
            }
        """
    }
    private fun createFragmentSource(spec: ShaderSpec, uniformsString: String): String {
        val inputAttributesString: String = spec.getIntermediateAttributes().joinToString("\n") { "in ${it.type} ${it.name};" }
        val outputAttributesString: String = spec.getOutputAttributes().joinToString("\n") { "out ${it.type} ${it.name};" }

        return """
            #version 330 core
            $inputAttributesString
            $outputAttributesString
            $uniformsString
            void main() {
                ${spec.getFragmentShaderBody()}
            }
        """
    }
    private fun configureInputAttributes(attributes: List<ShaderInputAttributeInfo<*>>, divisor: Int): Unit {
        val strideByteSize: Int = attributes.sumOf { it.handle.descriptor.type.getByteSize() }
        var offset: Long = 0L
        attributes.forEach {
            var location: Int = it.location
            val type: GLSLAttributeType = it.handle.descriptor.type
            val isNormalized: Boolean = it.handle.isNormalized
            repeat(type.getNumLocations()) {
                when (type.getComponentType()) {
                    GLSLPrimitive.INT, GLSLPrimitive.UINT -> glVertexAttribIPointer(
                        location, type.getComponentsPerLocation(), type.getComponentType().toGLEnum(),
                        strideByteSize, offset
                    )
                    GLSLPrimitive.FLOAT -> glVertexAttribPointer(
                        location, type.getComponentsPerLocation(), type.getComponentType().toGLEnum(), isNormalized,
                        strideByteSize, offset
                    )
                }
                glEnableVertexAttribArray(location)
                glVertexAttribDivisor(location, divisor)

                location++
                offset += type.getLocationByteSize()
            }
        }
    }
    private fun buildOpenGLShader(spec: ShaderSpec): Shader {
        var inputAttributeLocation: Int = 0

        var vertexAttributeLocation: Int = 0
        spec.getVertexAttributes().forEach {
            it.location = inputAttributeLocation
            it.bufferLocation = vertexAttributeLocation
            inputAttributeLocation += it.handle.descriptor.type.getNumLocations()
            vertexAttributeLocation += it.handle.descriptor.type.getNumComponents()
        }
        var instanceAttributeLocation: Int = 0
        spec.getInstanceAttributes().forEach {
            it.location = inputAttributeLocation
            it.bufferLocation = instanceAttributeLocation
            inputAttributeLocation += it.handle.descriptor.type.getNumLocations()
            instanceAttributeLocation += it.handle.descriptor.type.getNumComponents()
        }

        val vertexDataStrideSize: Int = spec.getVertexAttributes().sumOf { it.handle.descriptor.type.getNumComponents() }
        val verticesPerInstance: Int = 4
        val instanceDataStrideSize: Int = spec.getInstanceAttributes().sumOf { it.handle.descriptor.type.getNumComponents() }
        val uniformsString: String = spec.getUniforms().joinToString("\n") { "uniform ${it.handle.descriptor.type} ${it.name}; " }
        val shader: OpenGLShader = OpenGLShader(
            spec.getName(),
            spec.getVertexAttributes(), vertexDataStrideSize, verticesPerInstance,
            spec.getInstanceAttributes(), instanceDataStrideSize,
            spec.getUniforms(),
            this.createVertexSource(spec, uniformsString), this.createFragmentSource(spec, uniformsString),
            this.errorCallback
        )
        if (!shader.isValid()) { return BlankShader(spec.getName()) }

        shader.bindVertexBuffer()
        this.configureInputAttributes(spec.getVertexAttributes(), divisor = 0)
        shader.bindInstanceBuffer()
        this.configureInputAttributes(spec.getInstanceAttributes(), divisor = 1)

        return shader
    }
    override fun acquire(contextHandle: Long, spec: ShaderSpec): ShaderInstanceHandle {
        val representation: String = spec.getRepresentation(contextHandle)
        val shader: Shader = this.cache.getOrPut(representation) {
            val shader: Shader = this.buildOpenGLShader(spec)
            spec.getUniforms().forEach { it.applyDefault(shader) }
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
            this.cache.entries.removeIf { it.value == handle.shader }
            this.instanceHandles.remove(handle.shader)
        } else {
            handle.shader.removeInstance(handle.instanceI)
            handles.subList(handle.instanceI, handles.size - 1).forEach { it.instanceI-- }
        }
    }

    internal fun releaseAll(): Unit {
        this.cache.values.forEach { it.destroy() }
        this.cache.clear()
        this.instanceHandles.clear()
    }
}
