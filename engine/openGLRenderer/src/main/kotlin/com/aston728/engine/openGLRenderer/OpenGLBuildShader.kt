package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.rendererBase.*
import org.lwjgl.opengl.GL33C.*

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

private fun createVertexSource(spec: ShaderSpec, uniformsString: String): String {
    var inputAttributeLocation: Int = 0

    val vertexAttributesString: String = spec.getVertexAttributes().joinToString("\n") {
        val location: Int = inputAttributeLocation
        inputAttributeLocation += getAttributeTypeNumLocations(it.type)
        "layout (location = $location) in ${getAttributeTypeName(it.type)} $it;"
    }
    val instanceAttributesString: String = spec.getInstanceAttributes().joinToString("\n") {
        val location: Int = inputAttributeLocation
        inputAttributeLocation += getAttributeTypeNumLocations(it.type)
        "layout (location = $location) in ${getAttributeTypeName(it.type)} $it;"
    }
    val outputAttributesString: String = spec.getIntermediateAttributes().joinToString("\n") {
        "out ${getAttributeTypeName(it.type)} ${it.name};"
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
        "in ${getAttributeTypeName(it.type)} ${it.name};"
    }
    val outputAttributesString: String = spec.getOutputAttributes().joinToString("\n") {
        "out ${getAttributeTypeName(it.type)} ${it.name};"
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

private fun configureInputAttributes(shader: OpenGLShader): Unit {
    var location: Int = 0

    glBindBuffer(GL_ARRAY_BUFFER, shader.vertexBuffer)
    var vertexAttributeOffset: Long = 0
    shader.vertexAttributes.forEach { handle ->
        val numLocations: Int = getAttributeTypeNumLocations(handle.type)
        val componentsPerLocation: Int = handle.type.numComponents / numLocations
        val glPrimitive: Int = getAttributeGLPrimitive(handle.primitive)
        val offsetIncrement: Int = handle.primitive.byteSize * componentsPerLocation
        repeat(numLocations) {
            if (glPrimitive == GL_FLOAT || handle.normalization != ShaderAttributeNormalization.NONE) {
                glVertexAttribPointer(
                    location, componentsPerLocation, glPrimitive, handle.normalization == ShaderAttributeNormalization.NORMALIZED,
                    shader.vertexDataStrideSize, vertexAttributeOffset
                )
            } else {
                glVertexAttribIPointer(location, componentsPerLocation, glPrimitive, shader.vertexDataStrideSize, vertexAttributeOffset)
            }
            glEnableVertexAttribArray(location)

            location++
            vertexAttributeOffset += offsetIncrement
        }
    }

    glBindBuffer(GL_ARRAY_BUFFER, shader.instanceBuffer)
    var instanceAttributeOffset: Long = 0
    shader.instanceAttributes.forEach { handle ->
        val numLocations: Int = getAttributeTypeNumLocations(handle.type)
        val componentsPerLocation: Int = handle.type.numComponents / numLocations
        val glPrimitive: Int = getAttributeGLPrimitive(handle.primitive)
        val offsetIncrement: Int = handle.primitive.byteSize * componentsPerLocation
        repeat(numLocations) {
            if (glPrimitive == GL_FLOAT || glPrimitive == GL_HALF_FLOAT || handle.normalization != ShaderAttributeNormalization.NONE) {
                glVertexAttribPointer(
                    location, componentsPerLocation, glPrimitive, handle.normalization == ShaderAttributeNormalization.NORMALIZED,
                    shader.instanceDataStrideSize, instanceAttributeOffset
                )
            } else {
                glVertexAttribIPointer(location, componentsPerLocation, glPrimitive, shader.instanceDataStrideSize, instanceAttributeOffset)
            }
            glEnableVertexAttribArray(location)
            glVertexAttribDivisor(location, 1)

            location++
            instanceAttributeOffset += offsetIncrement
        }
    }
}
private fun createShaderState(shader: OpenGLShader): OpenGLShaderState {
    val state: OpenGLShaderState = OpenGLShaderState()
    glBindVertexArray(state.vao)
    configureInputAttributes(shader)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, shader.ebo)
    return state
}

internal fun buildShader(
    spec: ShaderSpec, samplerManager: OpenGLShaderSamplerManager,
    contextProvider: () -> OpenGLContext,
    errorCallback: (String) -> Unit
): Shader? {
    val vertexAttributes: List<ShaderVertexAttributeHandle<*>> = spec.getVertexAttributes()
    val instanceAttributes: List<ShaderInstanceAttributeHandle<*>> = spec.getInstanceAttributes()
    val uniforms: List<ShaderUniformInfo<*>> = spec.getUniforms()

    val lastInputAttributeLocation: Int =
        vertexAttributes.sumOf { getAttributeTypeNumLocations(it.type) } +
        instanceAttributes.sumOf { getAttributeTypeNumLocations(it.type) }
    if (lastInputAttributeLocation >= GL_MAX_VERTEX_ATTRIBS) {
        errorCallback("$spec is invalid, there are too many input attributes")
        return null
    }
    val numSampledImages: Int = uniforms.count { it.handle.type.isSampler }
    if (numSampledImages > GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS) {
        errorCallback("$spec is invalid, there are too many sampled images")
    }

    val uniformsString: String = uniforms.joinToString("\n") { "uniform ${getUniformTypeName(it.handle.type)} ${it.handle}; " }
    val stateProvider: (OpenGLContext, OpenGLShader) -> OpenGLShaderState = { context, shader ->
        context.getOrPutShaderState(shader) { createShaderState(shader) }
    }
    val shader: OpenGLShader = OpenGLShader(
        spec.getName(),
        vertexAttributes, instanceAttributes, uniforms, samplerManager,
        createVertexSource(spec, uniformsString), createFragmentSource(spec, uniformsString),
        contextProvider, stateProvider,
        errorCallback,
    )
    return if (!shader.exists()) { null } else { shader }
}
