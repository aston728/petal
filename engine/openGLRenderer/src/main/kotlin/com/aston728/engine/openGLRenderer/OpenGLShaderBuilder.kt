package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.rendererBase.*
import org.lwjgl.opengl.GL33C.*

internal class OpenGLShaderBuilder {
    private fun getAttributeGLPrimitive(primitive: ShaderAttributePrimitive): Int = when (primitive) {
        ShaderAttributePrimitive.Int -> GL_INT
        ShaderAttributePrimitive.Byte -> GL_BYTE
        ShaderAttributePrimitive.Short -> GL_SHORT
        ShaderAttributePrimitive.UInt -> GL_UNSIGNED_INT
        ShaderAttributePrimitive.UByte -> GL_UNSIGNED_BYTE
        ShaderAttributePrimitive.UShort -> GL_UNSIGNED_SHORT
        ShaderAttributePrimitive.Float -> GL_FLOAT
        ShaderAttributePrimitive.HalfFloat -> GL_HALF_FLOAT
    }
    private fun getAttributeTypeName(type: ShaderAttributeType): String = when (type) {
        ShaderAttributeType.Int -> "int"
        ShaderAttributeType.IVec2 -> "ivec2"
        ShaderAttributeType.IVec3 -> "ivec3"
        ShaderAttributeType.IVec4 -> "ivec4"
        ShaderAttributeType.UInt -> "uint"
        ShaderAttributeType.UIVec2 -> "uvec2"
        ShaderAttributeType.UIVec3 -> "uvec3"
        ShaderAttributeType.UIVec4 -> "uvec4"
        ShaderAttributeType.Float -> "float"
        ShaderAttributeType.Vec2 -> "vec2"
        ShaderAttributeType.Vec3 -> "vec3"
        ShaderAttributeType.Vec4 -> "vec4"
        ShaderAttributeType.Mat2 -> "mat2"
        ShaderAttributeType.Mat2x3 -> "mat2x3"
        ShaderAttributeType.Mat2x4 -> "mat2x4"
        ShaderAttributeType.Mat3x2 -> "mat3x2"
        ShaderAttributeType.Mat3 -> "mat3"
        ShaderAttributeType.Mat3x4 -> "mat3x4"
        ShaderAttributeType.Mat4x2 -> "mat4x2"
        ShaderAttributeType.Mat4x3 -> "mat4x3"
        ShaderAttributeType.Mat4 -> "mat4"
    }
    private fun getAttributeTypeNumLocations(type: ShaderAttributeType): Int = when (type) {
        ShaderAttributeType.Int, ShaderAttributeType.IVec2, ShaderAttributeType.IVec3, ShaderAttributeType.IVec4 -> 1
        ShaderAttributeType.UInt, ShaderAttributeType.UIVec2, ShaderAttributeType.UIVec3, ShaderAttributeType.UIVec4 -> 1
        ShaderAttributeType.Float, ShaderAttributeType.Vec2, ShaderAttributeType.Vec3, ShaderAttributeType.Vec4 -> 1
        ShaderAttributeType.Mat2, ShaderAttributeType.Mat2x3, ShaderAttributeType.Mat2x4 -> 2
        ShaderAttributeType.Mat3x2,ShaderAttributeType.Mat3, ShaderAttributeType.Mat3x4 -> 3
        ShaderAttributeType.Mat4x2, ShaderAttributeType.Mat4x3, ShaderAttributeType.Mat4 -> 4
    }
    private fun getUniformTypeName(type: ShaderUniformType): String = when (type) {
        ShaderUniformType.Int, ShaderUniformType.Bool -> "int"
        ShaderUniformType.IVec2, ShaderUniformType.BoolVec2 -> "ivec2"
        ShaderUniformType.IVec3, ShaderUniformType.BoolVec3 -> "ivec3"
        ShaderUniformType.IVec4, ShaderUniformType.BoolVec4 -> "ivec4"
        ShaderUniformType.UInt -> "uint"
        ShaderUniformType.UIVec2 -> "uvec2"
        ShaderUniformType.UIVec3 -> "uvec3"
        ShaderUniformType.UIVec4 -> "uvec4"
        ShaderUniformType.Float -> "float"
        ShaderUniformType.Vec2 -> "vec2"
        ShaderUniformType.Vec3 -> "vec3"
        ShaderUniformType.Vec4 -> "vec4"
        ShaderUniformType.Mat2, ShaderUniformType.TMat2 -> "mat2"
        ShaderUniformType.Mat2x3, ShaderUniformType.TMat2x3 -> "mat2x3"
        ShaderUniformType.Mat2x4, ShaderUniformType.TMat2x4 -> "mat2x4"
        ShaderUniformType.Mat3x2, ShaderUniformType.TMat3x2 -> "mat3x2"
        ShaderUniformType.Mat3, ShaderUniformType.TMat3 -> "mat3"
        ShaderUniformType.Mat3x4, ShaderUniformType.TMat3x4 -> "mat3x4"
        ShaderUniformType.Mat4x2, ShaderUniformType.TMat4x2 -> "mat4x2"
        ShaderUniformType.Mat4x3, ShaderUniformType.TMat4x3 -> "mat4x3"
        ShaderUniformType.Mat4, ShaderUniformType.TMat4 -> "mat4"
        ShaderUniformType.Image2DSampler -> "sampler2D"
        ShaderUniformType.Image2DArraySampler -> "sampler2DArray"
    }

    private fun createVertexSource(
        spec: ShaderSpec,
        vertexAttributeBindings: List<OpenGLShaderVertexAttributeBinding>, instanceAttributeBindings: List<OpenGLShaderInstanceAttributeBinding>,
        uniformsString: String
    ): String {
        val vertexAttributesString: String = vertexAttributeBindings.joinToString("\n") {
            "layout (location = ${it.location}) in ${this.getAttributeTypeName(it.handle.type)} ${it.name};"
        }
        val instanceAttributesString: String = instanceAttributeBindings.joinToString("\n") {
            "layout (location = ${it.location}) in ${this.getAttributeTypeName(it.handle.type)} ${it.name};"
        }
        val outputAttributesString: String = spec.getIntermediateAttributes().joinToString("\n") {
            "out ${this.getAttributeTypeName(it.type)} ${it.name};"
        }

        return """
            #version 330 core
            $vertexAttributesString
            $instanceAttributesString
            $outputAttributesString
            $uniformsString
            void main() {
                ${spec.getVertexShaderBody()}
                ${spec.getVertexShaderFooter()}
            }
        """
    }
    private fun createFragmentSource(spec: ShaderSpec, uniformsString: String): String {
        val inputAttributesString: String = spec.getIntermediateAttributes().joinToString("\n") {
            "in ${this.getAttributeTypeName(it.type)} ${it.name};"
        }
        val outputAttributesString: String = spec.getOutputAttributes().joinToString("\n") {
            "out ${this.getAttributeTypeName(it.type)} ${it.name};"
        }

        return """
            #version 330 core
            $inputAttributesString
            $outputAttributesString
            $uniformsString
            void main() {
                ${spec.getFragmentShaderBody()}
                ${spec.getFragmentShaderFooter()}
            }
        """
    }

    private fun configure(
        shader: OpenGLShader,
        vertexAttributeBindings: List<OpenGLShaderVertexAttributeBinding>, instanceAttributeBindings: List<OpenGLShaderInstanceAttributeBinding>,
    ): Unit {
        shader.bindVertexBuffer()
        val vertexDataStrideByteSize: Int = vertexAttributeBindings.sumOf { it.handle.primitive.byteSize * it.handle.type.numComponents }
        var vertexAttributeOffset: Long = 0
        vertexAttributeBindings.forEach { binding ->
            var location: Int = binding.location
            val numLocations: Int = this.getAttributeTypeNumLocations(binding.handle.type)
            val componentsPerLocation: Int = binding.handle.type.numComponents / numLocations
            val glPrimitive: Int = this.getAttributeGLPrimitive(binding.handle.primitive)
            val offsetIncrement: Int = binding.handle.primitive.byteSize * componentsPerLocation
            repeat(numLocations) {
                if (glPrimitive == GL_FLOAT || binding.handle.normalization != ShaderAttributeNormalization.NONE) {
                    glVertexAttribPointer(
                        location, componentsPerLocation, glPrimitive, binding.handle.normalization == ShaderAttributeNormalization.NORMALIZED,
                        vertexDataStrideByteSize, vertexAttributeOffset
                    )
                } else {
                    glVertexAttribIPointer(location, componentsPerLocation, glPrimitive, vertexDataStrideByteSize, vertexAttributeOffset)
                }
                glEnableVertexAttribArray(location)

                location++
                vertexAttributeOffset += offsetIncrement
            }
        }

        shader.bindInstanceBuffer()
        val instanceDataStrideByteSize: Int = instanceAttributeBindings.sumOf { it.handle.primitive.byteSize * it.handle.type.numComponents }
        var instanceAttributeOffset: Long = 0
        instanceAttributeBindings.forEach { binding ->
            var location: Int = binding.location
            val numLocations: Int = this.getAttributeTypeNumLocations(binding.handle.type)
            val componentsPerLocation: Int = binding.handle.type.numComponents / numLocations
            val glPrimitive: Int = this.getAttributeGLPrimitive(binding.handle.primitive)
            val offsetIncrement: Int = binding.handle.primitive.byteSize * componentsPerLocation
            repeat(numLocations) {
                if (glPrimitive == GL_FLOAT || glPrimitive == GL_HALF_FLOAT || binding.handle.normalization != ShaderAttributeNormalization.NONE) {
                    glVertexAttribPointer(
                        location, componentsPerLocation, glPrimitive, binding.handle.normalization == ShaderAttributeNormalization.NORMALIZED,
                        instanceDataStrideByteSize, instanceAttributeOffset
                    )
                } else {
                    glVertexAttribIPointer(location, componentsPerLocation, glPrimitive, instanceDataStrideByteSize, instanceAttributeOffset)
                }
                glEnableVertexAttribArray(location)
                glVertexAttribDivisor(location, 1)

                location++
                instanceAttributeOffset += offsetIncrement
            }
        }
    }
    internal fun build(spec: ShaderSpec, samplerManager: OpenGLShaderSamplerManager, errorCallback: (String) -> Unit): Shader? {
        var inputAttributeLocation: Int = 0

        var vertexAttributeLocation: Int = 0
        val vertexAttributeBindings: List<OpenGLShaderVertexAttributeBinding> = spec.getVertexAttributes().map {
            val binding: OpenGLShaderVertexAttributeBinding = OpenGLShaderVertexAttributeBinding(
                it.handle, it.name,
                inputAttributeLocation, vertexAttributeLocation,
            )
            inputAttributeLocation += this.getAttributeTypeNumLocations(it.handle.type)
            vertexAttributeLocation += it.handle.primitive.byteSize * it.handle.type.numComponents
            binding
        }
        val vertexDataStrideSize: Int = vertexAttributeBindings.sumOf { it.handle.primitive.byteSize * it.handle.type.numComponents }
        val verticesPerInstance: Int = 4

        var instanceAttributeLocation: Int = 0
        val instanceAttributeBindings: List<OpenGLShaderInstanceAttributeBinding> = spec.getInstanceAttributes().map {
            val binding: OpenGLShaderInstanceAttributeBinding = OpenGLShaderInstanceAttributeBinding(
                it.handle, it.name,
                inputAttributeLocation, instanceAttributeLocation,
            )
            inputAttributeLocation += this.getAttributeTypeNumLocations(it.handle.type)
            instanceAttributeLocation += it.handle.primitive.byteSize * it.handle.type.numComponents
            binding
        }
        val instanceDataStrideSize: Int = instanceAttributeBindings.sumOf { it.handle.primitive.byteSize * it.handle.type.numComponents }

        val uniforms: List<ShaderUniformInfo<*>> = spec.getUniforms()
        if (inputAttributeLocation >= GL_MAX_VERTEX_ATTRIBS) {
            errorCallback("$spec is invalid, there are too many input attributes")
            return null
        }
        if (uniforms.count { it.handle.type.isSampler } > GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS) {
            errorCallback("$spec is invalid, there are too many sampled images")
        }

        val uniformsString: String = uniforms.joinToString("\n") { "uniform ${this.getUniformTypeName(it.handle.type)} ${it.name}; " }
        val shader: OpenGLShader = OpenGLShader(
            spec.getName(),
            vertexAttributeBindings, vertexDataStrideSize, verticesPerInstance,
            instanceAttributeBindings, instanceDataStrideSize,
            uniforms, samplerManager,
            this.createVertexSource(spec, vertexAttributeBindings, instanceAttributeBindings, uniformsString),
            this.createFragmentSource(spec, uniformsString),
            errorCallback
        )

        if (!shader.isValid()) { return null }
        this.configure(shader, vertexAttributeBindings, instanceAttributeBindings)
        return shader
    }
}
