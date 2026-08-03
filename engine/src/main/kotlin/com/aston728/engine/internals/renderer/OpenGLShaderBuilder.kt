package com.aston728.engine.internals.renderer

import com.aston728.engine.renderer.Shader
import com.aston728.engine.renderer.ShaderBuilder
import com.aston728.engine.renderer.ShaderSpec
import com.aston728.engine.types.ErrorHandler
import org.lwjgl.opengl.GL20C.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20C.glVertexAttribPointer
import org.lwjgl.opengl.GL30C.glVertexAttribIPointer
import org.lwjgl.opengl.GL33C.glVertexAttribDivisor
import org.lwjgl.opengl.GL20C.GL_MAX_VERTEX_ATTRIBS

class OpenGLShaderBuilder : ShaderBuilder {
    private fun createVertexSource(
        spec: ShaderSpec,
        vertexAttributeBindings: List<ShaderVertexAttributeBinding>, instanceAttributeBindings: List<ShaderInstanceAttributeBinding>,
        uniformsString: String
    ): String {
        val vertexAttributesString: String = vertexAttributeBindings.joinToString("\n") {
            "layout (location = ${it.location}) in ${it.handle.descriptor.type} ${it.name};"
        }
        val instanceAttributesString: String = instanceAttributeBindings.joinToString("\n") {
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
    private fun configureInputAttribute(
        location: Int, type: GLSLAttributeType, isNormalized: Boolean, strideByteSize: Int, offset: Long,
        divisor: Int,
    ): Unit {
        var location: Int = location
        var offset: Long = offset
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
    private fun configure(
        shader: OpenGLShader,
        vertexAttributeBindings: List<ShaderVertexAttributeBinding>, instanceAttributeBindings: List<ShaderInstanceAttributeBinding>,
    ): Unit {
        shader.bindVertexBuffer()
        val vertexDataStrideByteSize: Int = vertexAttributeBindings.sumOf { it.handle.descriptor.type.getByteSize() }
        var vertexAttributeOffset: Long = 0
        vertexAttributeBindings.forEach {
            this.configureInputAttribute(
                it.location, it.handle.descriptor.type, it.handle.isNormalized, vertexDataStrideByteSize, vertexAttributeOffset,
                divisor = 0
            )
            vertexAttributeOffset += it.handle.descriptor.type.getLocationByteSize() * it.handle.descriptor.type.getNumLocations()
        }

        shader.bindInstanceBuffer()
        val instanceDataStrideByteSize: Int = instanceAttributeBindings.sumOf { it.handle.descriptor.type.getByteSize() }
        var instanceAttributeOffset: Long = 0
        instanceAttributeBindings.forEach {
            this.configureInputAttribute(
                it.location, it.handle.descriptor.type, it.handle.isNormalized, instanceDataStrideByteSize, instanceAttributeOffset,
                divisor = 1
            )
            instanceAttributeOffset += it.handle.descriptor.type.getLocationByteSize() * it.handle.descriptor.type.getNumLocations()
        }
    }
    override fun build(spec: ShaderSpec, errorCallback: ErrorHandler): Shader? {
        var inputAttributeLocation: Int = 0

        var vertexAttributeLocation: Int = 0
        val vertexAttributeBindings: List<ShaderVertexAttributeBinding> = spec.getVertexAttributes().map {
            val binding: ShaderVertexAttributeBinding = ShaderVertexAttributeBinding(
                it.handle, it.name,
                inputAttributeLocation, vertexAttributeLocation,
            )
            inputAttributeLocation += it.handle.descriptor.type.getNumLocations()
            vertexAttributeLocation += it.handle.descriptor.type.getNumComponents()
            binding
        }
        val vertexDataStrideSize: Int = vertexAttributeBindings.sumOf { it.handle.descriptor.type.getNumComponents() }
        val verticesPerInstance: Int = 4

        var instanceAttributeLocation: Int = 0
        val instanceAttributeBindings: List<ShaderInstanceAttributeBinding> = spec.getInstanceAttributes().map {
            val binding: ShaderInstanceAttributeBinding = ShaderInstanceAttributeBinding(
                it.handle, it.name,
                inputAttributeLocation, instanceAttributeLocation,
            )
            inputAttributeLocation += it.handle.descriptor.type.getNumLocations()
            instanceAttributeLocation += it.handle.descriptor.type.getNumComponents()
            binding
        }
        val instanceDataStrideSize: Int = instanceAttributeBindings.sumOf { it.handle.descriptor.type.getNumComponents() }

        if (inputAttributeLocation >= GL_MAX_VERTEX_ATTRIBS) {
            errorCallback("$spec is invalid, there are too many input attributes")
            return null
        }

        val uniformsString: String = spec.getUniforms().joinToString("\n") { "uniform ${it.handle.descriptor.type} ${it.name}; " }
        val shader: OpenGLShader = OpenGLShader(
            spec.getName(),
            vertexAttributeBindings, vertexDataStrideSize, verticesPerInstance,
            instanceAttributeBindings, instanceDataStrideSize,
            spec.getUniforms(),
            this.createVertexSource(spec, vertexAttributeBindings, instanceAttributeBindings, uniformsString),
            this.createFragmentSource(spec, uniformsString),
            errorCallback
        )

        if (!shader.isValid()) { return null }
        this.configure(shader, vertexAttributeBindings, instanceAttributeBindings)
        return shader
    }
}
