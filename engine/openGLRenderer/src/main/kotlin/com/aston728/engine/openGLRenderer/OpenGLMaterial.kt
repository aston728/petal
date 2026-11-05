package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.math.*
import com.aston728.engine.core.rendererBase.*
import org.lwjgl.opengl.GL11C.GL_TEXTURE_2D
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL21C.*
import org.lwjgl.opengl.GL30C.*

private fun boolToInt(bool: Boolean): Int = if (bool) { 1 } else { 0 }
private class OpenGLShaderTextureBinding(var texture: OpenGLTexture, var type: Int, var sampler: Int)

internal class OpenGLMaterial(
    name: String,
    uniforms: List<ShaderUniformInfo<*>>, private val samplerManager: OpenGLShaderSamplerManager,
    errorCallback: (String) -> Unit,
    private val contextProvider: () -> OpenGLContext, private val contextsProvider: () -> List<OpenGLContext>,
    private val programProvider: (Shader) -> Int, // TODO
) : Material(name, uniforms, errorCallback) {
    private var uniformMappings: Map<ShaderUniformHandle<*>, Int> = emptyMap()
    private val textureUnitMappings: Map<ShaderUniformHandle<*>, Int> by lazy { buildMap {
        var unit: Int = 0
        uniforms.forEach {
            if (it.handle.type.isSampler) {
                this[it.handle] = unit
                unit++
            }
        }
    }}
    private val textureBindings: Array<OpenGLShaderTextureBinding> by lazy {
        Array(this.textureUnitMappings.size) { OpenGLShaderTextureBinding(OpenGLTexture(0), 0, 0) }
    }

    override fun onAssignShader(shader: Shader) {
        val program: Int = this.programProvider(shader)
        this.uniformMappings =
            if (program != OpenGLShader.INVALID_PROGRAM) {
                this._uniforms.associate { it.handle to glGetUniformLocation(program, it.handle.name) }
            } else {
                emptyMap()
            }
    }
    override fun onBind(): Unit {
        val context: OpenGLContext = this.contextProvider()
        val contexts: List<OpenGLContext> = this.contextsProvider()
        this.textureBindings.forEachIndexed { i, binding ->
            if (binding.texture.updateQueue.isNotEmpty()) {
                binding.texture.updateQueue.forEach { it(binding.texture) }
                contexts.forEach { it.invalidateTextureUnit(i) }
                binding.texture.updateQueue.clear()
            }
            context.bindTexture(i, binding.texture.value, binding.type, binding.sampler)
        }
    }
    override fun <T> updateUniform(info: ShaderUniform<T>): Unit {
        val location: Int? = this.uniformMappings[info.handle]
        if (location == null) {
            this._errorCallback("Cannot set uniform '${info.handle}', the handle isn't registered to $this")
        } else {
            val value: T = info.value
            when (info.handle.type) {
                ShaderUniformType.Int -> { glUniform1i(location, value as Int) }
                ShaderUniformType.IVec2 -> { glUniform2i(location, (value as IVec2).first, value.second) }
                ShaderUniformType.IVec3 -> { glUniform3i(location, (value as IVec3).first, value.second, value.third) }
                ShaderUniformType.IVec4 -> { glUniform4i(location, (value as IVec4).first, value.second, value.third, value.fourth) }
                ShaderUniformType.Bool -> {
                    value as Boolean
                    glUniform1i(location, boolToInt(value))
                }
                ShaderUniformType.BoolVec2 -> {
                    value as BoolVec2
                    glUniform2i(location, boolToInt(value.first), boolToInt(value.second))
                }
                ShaderUniformType.BoolVec3 -> {
                    value as BoolVec3
                    glUniform3i(location, boolToInt(value.first), boolToInt(value.second), boolToInt(value.third))
                }
                ShaderUniformType.BoolVec4 -> {
                    value as BoolVec4
                    glUniform4i(location, boolToInt(value.first), boolToInt(value.second), boolToInt(value.third), boolToInt(value.fourth))
                }
                ShaderUniformType.UInt -> { glUniform1ui(location, (value as UInt).toInt()) }
                ShaderUniformType.UIVec2 -> { glUniform2ui(location, (value as UIVec2).first.toInt(), value.second.toInt()) }
                ShaderUniformType.UIVec3 -> { glUniform3ui(location, (value as UIVec3).first.toInt(), value.second.toInt(), value.third.toInt()) }
                ShaderUniformType.UIVec4 -> { glUniform4ui(location, (value as UIVec4).first.toInt(), value.second.toInt(), value.third.toInt(), value.fourth.toInt()) }
                ShaderUniformType.Float -> { glUniform1f(location, value as Float) }
                ShaderUniformType.Vec2 -> { glUniform2f(location, (value as Vec2).first, value.second) }
                ShaderUniformType.Vec3 -> { glUniform3f(location, (value as Vec3).first, value.second, value.third) }
                ShaderUniformType.Vec4 -> { glUniform4f(location, (value as Vec4).first, value.second, value.third, value.fourth) }
                ShaderUniformType.Mat2 -> { glUniformMatrix2fv(location, false, (value as Mat2).toFlatArray()) }
                ShaderUniformType.TMat2 -> { glUniformMatrix2fv(location, true, (value as Mat2).toFlatArray()) }
                ShaderUniformType.Mat2x3 -> { glUniformMatrix2x3fv(location, false, (value as Mat2x3).toFlatArray()) }
                ShaderUniformType.TMat2x3 -> { glUniformMatrix2x3fv(location, true, (value as Mat2x3).toFlatArray()) }
                ShaderUniformType.Mat2x4 -> { glUniformMatrix2x4fv(location, false, (value as Mat2x4).toFlatArray()) }
                ShaderUniformType.TMat2x4 -> { glUniformMatrix2x4fv(location, true, (value as Mat2x4).toFlatArray()) }
                ShaderUniformType.Mat3x2 -> { glUniformMatrix3x2fv(location, false, (value as Mat3x2).toFlatArray()) }
                ShaderUniformType.TMat3x2 -> { glUniformMatrix3x2fv(location, true, (value as Mat3x2).toFlatArray()) }
                ShaderUniformType.Mat3 -> { glUniformMatrix3fv(location, false, (value as Mat3).toFlatArray()) }
                ShaderUniformType.TMat3 -> { glUniformMatrix3fv(location, true, (value as Mat3).toFlatArray()) }
                ShaderUniformType.Mat3x4 -> { glUniformMatrix3x4fv(location, false, (value as Mat3x4).toFlatArray()) }
                ShaderUniformType.TMat3x4 -> { glUniformMatrix3x4fv(location, true, (value as Mat3x4).toFlatArray()) }
                ShaderUniformType.Mat4x2 -> { glUniformMatrix4x2fv(location, false, (value as Mat4x2).toFlatArray()) }
                ShaderUniformType.TMat4x2 -> { glUniformMatrix4x2fv(location, true, (value as Mat4x2).toFlatArray()) }
                ShaderUniformType.Mat4x3 -> { glUniformMatrix4x3fv(location, false, (value as Mat4x3).toFlatArray()) }
                ShaderUniformType.TMat4x3 -> { glUniformMatrix4x3fv(location, true, (value as Mat4x3).toFlatArray()) }
                ShaderUniformType.Mat4 -> { glUniformMatrix4fv(location, false, (value as Mat4).toFlatArray()) }
                ShaderUniformType.TMat4 -> {
                    glUniformMatrix4fv(location, true, (value as Mat4).toFlatArray())
                }
                ShaderUniformType.Image2DSampler -> {
                    value as ShaderSampledImage2D
                    val unit: Int = this.textureUnitMappings[info.handle]!!
                    this.textureBindings[unit].texture = this.samplerManager.getTexture2D(value.img)
                    this.textureBindings[unit].type = GL_TEXTURE_2D
                    this.textureBindings[unit].sampler = this.samplerManager.getGLSampler(value.sampler)
                    glUniform1i(location, unit)
                }

                ShaderUniformType.Image2DArraySampler -> {
                    value as ShaderSampledImage2DArray
                    val unit: Int = this.textureUnitMappings[info.handle]!!
                    this.textureBindings[unit].texture = this.samplerManager.getTexture2DArray(value.imgs)
                    this.textureBindings[unit].type = GL_TEXTURE_2D_ARRAY
                    this.textureBindings[unit].sampler = this.samplerManager.getGLSampler(value.sampler)
                    glUniform1i(location, unit)
                }
            }
        }
    }
    override fun onFree(): Unit {}
}
