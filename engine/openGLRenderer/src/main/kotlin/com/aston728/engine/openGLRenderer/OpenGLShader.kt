package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.math.*
import com.aston728.engine.core.rendererBase.*
import com.aston728.engine.core.types.ErrorHandler
import org.lwjgl.opengl.GL33C.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

private fun boolToInt(bool: Boolean): Int = if (bool) { 1 } else { 0 }
private class OpenGLShaderAttributeData(val name: String, val location: Int)

internal class OpenGLShader(
    name: String,
    vertexAttributesInfo: List<OpenGLShaderVertexAttributeBinding>, private val vertexDataStrideSize: Int, private val verticesPerInstance: Int,
    instanceAttributesInfo: List<OpenGLShaderInstanceAttributeBinding>, private val instanceDataStrideSize: Int,
    uniformsInfo: List<ShaderUniformInfo<*>>, private val samplerManager: OpenGLShaderSamplerManager,
    vertexSource: String, fragmentSource: String,
    private val errorCallback: ErrorHandler,
) : Shader(name) {
    private companion object {
        private const val INVALID_PROGRAM: Int = -1
    }

    private var vertexData: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    private var instanceData: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    private var isVertexDataDirty: Boolean = false
    private var isInstanceDataDirty: Boolean = false
    private val uniformUpdatesQueue: MutableList<() -> Unit> = mutableListOf()

    private val vao: Int = glGenVertexArrays()
    private val vertexBuffer: Int = glGenBuffers()
    private val instanceBuffer: Int = glGenBuffers()
    private val ebo: Int = glGenBuffers()
    private var program: Int = this.createProgram(vertexSource, fragmentSource)

    private val vertexAttributeMappings: Map<ShaderVertexAttributeHandle<*>, OpenGLShaderAttributeData> by lazy {
        vertexAttributesInfo.associate { it.handle to OpenGLShaderAttributeData(it.name, it.bufferLocation) }
    }
    private val instanceAttributeMappings: Map<ShaderInstanceAttributeHandle<*>, OpenGLShaderAttributeData> by lazy {
        instanceAttributesInfo.associate { it.handle to OpenGLShaderAttributeData(it.name, it.bufferLocation) }
    }
    private val uniformMappings: Map<ShaderUniformHandle<*>, OpenGLShaderAttributeData> =
        if (this.isValid()) {
            uniformsInfo.associate { it.handle to OpenGLShaderAttributeData(it.name, glGetUniformLocation(this.program, it.name)) }
        } else {
            emptyMap()
        }

    private val textureUnitMappings: Map<ShaderUniformHandle<*>, Int> by lazy { buildMap {
        uniformsInfo.forEachIndexed { i, info ->
            if (info.handle.type.isSampler) { this[info.handle] = i }
        }
    }}
    private val textureBindings: IntArray by lazy {
        IntArray(this.textureUnitMappings.size) { 0 }
    }
    private val textureTypeBindings: IntArray by lazy {
        IntArray(this.textureUnitMappings.size) { 0 }
    }
    private val samplerBindings: IntArray by lazy {
        IntArray(this.textureUnitMappings.size) { 0 }
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader: Int = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, vertexSource)
        glCompileShader(vertexShader)
        val vertexShaderError: String = glGetShaderInfoLog(vertexShader)
        if (!vertexShaderError.isEmpty()) { // Exit on warning too
            this.errorCallback("Failed to create vertex shader for $this: $vertexShaderError")

            glDeleteShader(vertexShader)
            return OpenGLShader.INVALID_PROGRAM
        }

        val fragmentShader: Int = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentSource)
        glCompileShader(fragmentShader)
        val fragmentShaderError: String = glGetShaderInfoLog(fragmentShader)
        if (!fragmentShaderError.isEmpty()) { // Exit on warning too
            this.errorCallback("Failed to create fragment shader for $this: $fragmentShaderError")

            glDeleteShader(vertexShader)
            glDeleteShader(fragmentShader)
            return OpenGLShader.INVALID_PROGRAM
        }

        val program: Int = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
        val programError: String = glGetProgramInfoLog(program)
        if (!programError.isEmpty()) { // Exit on warning too
            this.errorCallback("Failed to create program for $this: $programError")

            glDeleteShader(vertexShader)
            glDeleteShader(fragmentShader)
            glDeleteProgram(program)
            return OpenGLShader.INVALID_PROGRAM
        }

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)

        glUseProgram(this.program)
        glBindVertexArray(this.vao)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intArrayOf(0, 1, 2, 1, 2, 3), GL_STATIC_DRAW)

        return program
    }

    override fun isValid(): Boolean = this.program != OpenGLShader.INVALID_PROGRAM

    override fun onAddInstance(): Unit {
        glUseProgram(this.program)
        glBindVertexArray(this.vao)

        this.vertexData = ByteBuffer
            .allocateDirect(this.vertexData.capacity() + this.vertexDataStrideSize * this.verticesPerInstance)
            .order(ByteOrder.nativeOrder())
            .put(this.vertexData)
            .rewind()
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
        glBufferData(GL_ARRAY_BUFFER, this.vertexData, GL_STATIC_DRAW)

        this.instanceData = ByteBuffer
            .allocateDirect(this.instanceData.capacity() + this.instanceDataStrideSize)
            .order(ByteOrder.nativeOrder())
            .put(this.instanceData)
            .rewind()
        glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
        glBufferData(GL_ARRAY_BUFFER, this.instanceData, GL_STATIC_DRAW)
    }
    private fun removeBufferSlice(buffer: ByteBuffer, startI: Int, endI: Int): ByteBuffer {
        val removedLength: Int = endI - startI + 1
        val newBuffer: ByteBuffer = ByteBuffer.allocateDirect(buffer.capacity() - removedLength).order(ByteOrder.nativeOrder())

        buffer.position(0).limit(startI)
        newBuffer.put(buffer)
        buffer.position(endI + 1).limit(buffer.capacity())
        newBuffer.put(buffer)

        return newBuffer.flip()
    }
    override fun onRemoveInstance(i: Int): Unit {
        glUseProgram(this.program)
        glBindVertexArray(this.vao)

        val vertexStartI: Int = this.vertexDataStrideSize * this.verticesPerInstance * i
        val vertexEndI: Int = vertexStartI + this.vertexDataStrideSize * this.verticesPerInstance
        this.vertexData = this.removeBufferSlice(this.vertexData, vertexStartI, vertexEndI)
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
        glBufferData(GL_ARRAY_BUFFER, this.vertexData, GL_STATIC_DRAW)

        val instanceStartI: Int = this.instanceDataStrideSize * i
        val instanceEndI: Int = instanceStartI + this.instanceDataStrideSize
        this.instanceData = this.removeBufferSlice(this.instanceData, instanceStartI, instanceEndI)
        glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
        glBufferData(GL_ARRAY_BUFFER, this.instanceData, GL_STATIC_DRAW)
    }
    override fun <T> onSetVertexAttribute(
        handle: ShaderVertexAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        values: Array<T>
    ): Unit {
        val data: OpenGLShaderAttributeData? = this.vertexAttributeMappings[handle]
        if (data == null) {
            this.errorCallback("Cannot set vertex attribute, this handle isn't registered to $this")
        } else if (!this.isValid()) {
            this.errorCallback("Cannot set vertex attribute '${data.name}', $this is invalid")
        } else if (values.size != this.verticesPerInstance) {
            this.errorCallback("Cannot set vertex attribute '${data.name}' of $this, expected ${this.verticesPerInstance} values, got ${values.size}")
        } else {
            var i: Int = this.vertexDataStrideSize * this.verticesPerInstance * instanceI + data.location
            values.forEach {
                updater(this.vertexData, i, it)
                i += this.vertexDataStrideSize
            }
            this.isVertexDataDirty = true
        }
    }
    override fun <T> onSetInstanceAttribute(
        handle: ShaderInstanceAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        value: T
    ): Unit {
        val data: OpenGLShaderAttributeData? = this.instanceAttributeMappings[handle]
        if (data == null) {
            this.errorCallback("Cannot set instance attribute, this handle isn't registered to $this")
        } else if (!this.isValid()) {
            this.errorCallback("Cannot set instance attribute '${data.name}', $this is invalid")
        } else {
            val i: Int = this.instanceDataStrideSize * instanceI + data.location
            updater(this.instanceData, i, value)
            this.isInstanceDataDirty = true
        }
    }
    private fun <T> getUniformUpdater(handle: ShaderUniformHandle<T>, location: Int, value: T): () -> Unit = when (handle.type) {
        ShaderUniformType.Int -> {{ glUniform1i(location, value as Int) }}
        ShaderUniformType.IVec2 -> {{ glUniform2i(location, (value as IVec2).first, value.second) }}
        ShaderUniformType.IVec3 -> {{ glUniform3i(location, (value as IVec3).first, value.second, value.third) }}
        ShaderUniformType.IVec4 -> {{ glUniform4i(location, (value as IVec4).first, value.second, value.third, value.fourth) }}
        ShaderUniformType.Bool -> {{
            value as Boolean
            glUniform1i(location, boolToInt(value))
        }}
        ShaderUniformType.BoolVec2 -> {{
            value as BoolVec2
            glUniform2i(location, boolToInt(value.first), boolToInt(value.second))
        }}
        ShaderUniformType.BoolVec3 -> {{
            value as BoolVec3
            glUniform3i(location, boolToInt(value.first), boolToInt(value.second), boolToInt(value.third))
        }}
        ShaderUniformType.BoolVec4 -> {{
            value as BoolVec4
            glUniform4i(location, boolToInt(value.first), boolToInt(value.second), boolToInt(value.third), boolToInt(value.fourth))
        }}
        ShaderUniformType.UInt -> {{ glUniform1ui(location, (value as UInt).toInt()) }}
        ShaderUniformType.UIVec2 -> {{ glUniform2ui(location, (value as UIVec2).first.toInt(), value.second.toInt()) }}
        ShaderUniformType.UIVec3 -> {{ glUniform3ui(location, (value as UIVec3).first.toInt(), value.second.toInt(), value.third.toInt()) }}
        ShaderUniformType.UIVec4 -> {{ glUniform4ui(location, (value as UIVec4).first.toInt(), value.second.toInt(), value.third.toInt(), value.fourth.toInt()) }}
        ShaderUniformType.Float -> {{ glUniform1f(location, value as Float) }}
        ShaderUniformType.Vec2 -> {{ glUniform2f(location, (value as Vec2).first, value.second) }}
        ShaderUniformType.Vec3 -> {{ glUniform3f(location, (value as Vec3).first, value.second, value.third) }}
        ShaderUniformType.Vec4 -> {{ glUniform4f(location, (value as Vec4).first, value.second, value.third, value.fourth) }}
        ShaderUniformType.Mat2 -> {{ glUniformMatrix2fv(location, false, (value as Mat2).toFlatArray()) }}
        ShaderUniformType.TMat2 -> {{ glUniformMatrix2fv(location, true, (value as Mat2).toFlatArray()) }}
        ShaderUniformType.Mat2x3 -> {{ glUniformMatrix2x3fv(location, false, (value as Mat2x3).toFlatArray()) }}
        ShaderUniformType.TMat2x3 -> {{ glUniformMatrix2x3fv(location, true, (value as Mat2x3).toFlatArray()) }}
        ShaderUniformType.Mat2x4 -> {{ glUniformMatrix2x4fv(location, false, (value as Mat2x4).toFlatArray()) }}
        ShaderUniformType.TMat2x4 -> {{ glUniformMatrix2x4fv(location, true, (value as Mat2x4).toFlatArray()) }}
        ShaderUniformType.Mat3x2 -> {{ glUniformMatrix3x2fv(location, false, (value as Mat3x2).toFlatArray()) }}
        ShaderUniformType.TMat3x2 -> {{ glUniformMatrix3x2fv(location, true, (value as Mat3x2).toFlatArray()) }}
        ShaderUniformType.Mat3 -> {{ glUniformMatrix3fv(location, false, (value as Mat3).toFlatArray()) }}
        ShaderUniformType.TMat3 -> {{ glUniformMatrix3fv(location, true, (value as Mat3).toFlatArray()) }}
        ShaderUniformType.Mat3x4 -> {{ glUniformMatrix3x4fv(location, false, (value as Mat3x4).toFlatArray()) }}
        ShaderUniformType.TMat3x4 -> {{ glUniformMatrix3x4fv(location, true, (value as Mat3x4).toFlatArray()) }}
        ShaderUniformType.Mat4x2 -> {{ glUniformMatrix4x2fv(location, false, (value as Mat4x2).toFlatArray()) }}
        ShaderUniformType.TMat4x2 -> {{ glUniformMatrix4x2fv(location, true, (value as Mat4x2).toFlatArray()) }}
        ShaderUniformType.Mat4x3 -> {{ glUniformMatrix4x3fv(location, false, (value as Mat4x3).toFlatArray()) }}
        ShaderUniformType.TMat4x3 -> {{ glUniformMatrix4x3fv(location, true, (value as Mat4x3).toFlatArray()) }}
        ShaderUniformType.Mat4 -> {{ glUniformMatrix4fv(location, false, (value as Mat4).toFlatArray()) }}
        ShaderUniformType.TMat4 -> {{ glUniformMatrix4fv(location, true, (value as Mat4).toFlatArray()) }}
        ShaderUniformType.Image2DSampler -> {{
            value as ShaderSampledImage2D
            val unit: Int = this.textureUnitMappings[handle]!!
            this.textureBindings[unit] = this.samplerManager.getTexture2D(value.img)
            this.textureTypeBindings[unit] = GL_TEXTURE_2D
            this.samplerBindings[unit] = this.samplerManager.getGLSampler(value.sampler)
            glUniform1i(location, unit)
        }}
        ShaderUniformType.Image2DArraySampler -> {{
            value as ShaderSampledImage2DArray
            val unit: Int = this.textureUnitMappings[handle]!!
            this.textureBindings[unit] = this.samplerManager.getTexture2DArray(value.imgs)
            this.textureTypeBindings[unit] = GL_TEXTURE_2D_ARRAY
            this.samplerBindings[unit] = this.samplerManager.getGLSampler(value.sampler)
            glUniform1i(location, unit)
        }}
    }
    override fun <T> setUniform(handle: ShaderUniformHandle<T>, value: T): Unit {
        val data: OpenGLShaderAttributeData? = this.uniformMappings[handle]
        if (data == null) {
            this.errorCallback("Cannot set uniform, this handle isn't registered to $this")
        } else if (!this.isValid()) {
            this.errorCallback("Cannot set uniform '${data.name}', $this is invalid")
        } else {
            this.uniformUpdatesQueue.add(this.getUniformUpdater(handle, data.location, value))
        }
    }

    internal fun bindVertexBuffer(): Unit {
        glUseProgram(this.program)
        glBindVertexArray(this.vao)
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
    }
    internal fun bindInstanceBuffer(): Unit {
        glUseProgram(this.program)
        glBindVertexArray(this.vao)
        glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
    }
    override fun onDestroy(): Unit {
        glDeleteVertexArrays(this.vao)
        glDeleteBuffers(this.vertexBuffer)
        glDeleteBuffers(this.instanceBuffer)
        glDeleteBuffers(this.ebo)
        glDeleteProgram(this.program)
        this.program = OpenGLShader.INVALID_PROGRAM
    }
    override fun onDraw(graphicsContext: GraphicsContext): Unit { // TODO
        glUseProgram(this.program)
        glBindVertexArray(this.vao)

        if (this.isVertexDataDirty) {
            glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
            glBufferSubData(GL_ARRAY_BUFFER, 0, this.vertexData)
            this.isVertexDataDirty = false
        }
        if (this.isInstanceDataDirty) {
            glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
            glBufferSubData(GL_ARRAY_BUFFER, 0, this.instanceData)
            this.isInstanceDataDirty = false
        }
        if (!this.uniformUpdatesQueue.isEmpty()) {
            this.uniformUpdatesQueue.forEach { it() }
            this.uniformUpdatesQueue.clear()
        }

        this.textureBindings.indices.forEach {
            graphicsContext.bindTexture(it, this.textureBindings[it], this.textureTypeBindings[it], this.samplerBindings[it])
        }

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
    }
}
