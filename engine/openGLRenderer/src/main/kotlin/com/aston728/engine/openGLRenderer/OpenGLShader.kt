package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.math.*
import com.aston728.engine.core.rendererBase.*
import org.lwjgl.opengl.GL33C.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

private fun boolToInt(bool: Boolean): Int = if (bool) { 1 } else { 0 }
private class BufferRange(val start: Int, val end: Int)
private class OpenGLShaderTextureBinding(var texture: Int, var type: Int, var sampler: Int)

internal class OpenGLShader(
    name: String,
    internal val vertexAttributes: List<ShaderVertexAttributeHandle<*>>, internal val instanceAttributes: List<ShaderInstanceAttributeHandle<*>>,
    uniformsInfo: List<ShaderUniformInfo<*>>, private val samplerManager: OpenGLShaderSamplerManager,
    vertexSource: String, fragmentSource: String,
    private val contextProvider: () -> OpenGLContext, private val stateProvider: (OpenGLContext, OpenGLShader) -> OpenGLShaderState,
    private val errorCallback: (String) -> Unit,
) : Shader(name) {
    companion object {
        private const val INVALID_PROGRAM: Int = -1
    }

    private var vertexData: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    internal val vertexDataStrideSize: Int = this.vertexAttributes.sumOf { it.primitive.byteSize * it.type.numComponents }
    private val verticesPerInstance: Int = 4

    private var instanceData: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    internal val instanceDataStrideSize: Int = this.instanceAttributes.sumOf { it.primitive.byteSize * it.type.numComponents }

    private var dataFreeSlots: MutableList<Int> = mutableListOf()
    private var vertexDataDirtyRanges: MutableList<BufferRange> = mutableListOf()
    private var instanceDataDirtyRanges: MutableList<BufferRange> = mutableListOf()
    private val uniformUpdatesQueue: MutableList<() -> Unit> = mutableListOf()

    internal val vertexBuffer: Int = glGenBuffers()
    internal val instanceBuffer: Int = glGenBuffers()
    internal val ebo: Int = this.createEbo()
    internal var program: Int = this.createProgram(vertexSource, fragmentSource)

    var vertexAttributeLocation: Int = 0
    private val vertexAttributeMappings: Map<ShaderVertexAttributeHandle<*>, Int> = this.vertexAttributes.associateWith {
        val location: Int = vertexAttributeLocation
        vertexAttributeLocation += it.primitive.byteSize * it.type.numComponents
        location
    }
    var instanceAttributeLocation: Int = 0
    private val instanceAttributeMappings: Map<ShaderInstanceAttributeHandle<*>, Int> = this.instanceAttributes.associateWith {
        val location: Int = instanceAttributeLocation
        instanceAttributeLocation += it.primitive.byteSize * it.type.numComponents
        location
    }

    private val uniformMappings: Map<ShaderUniformHandle<*>, Int> =
        if (this.exists()) {
            uniformsInfo.associate { it.handle to glGetUniformLocation(this.program, it.handle.name) }
        } else {
            emptyMap()
        }
    private val textureUnitMappings: Map<ShaderUniformHandle<*>, Int> by lazy { buildMap {
        uniformsInfo.forEachIndexed { i, info ->
            if (info.handle.type.isSampler) { this[info.handle] = i }
        }
    }}
    private val textureBindings: Array<OpenGLShaderTextureBinding> by lazy {
        Array(this.textureUnitMappings.size) { OpenGLShaderTextureBinding(0, 0, 0) }
    }

    override fun exists(): Boolean = this.program != OpenGLShader.INVALID_PROGRAM

    private fun createEbo(): Int {
        val ebo: Int = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intArrayOf(0, 1, 2, 1, 2, 3), GL_STATIC_DRAW)
        return ebo
    }
    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader: Int = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, vertexSource)
        glCompileShader(vertexShader)
        val vertexShaderError: String = glGetShaderInfoLog(vertexShader)
        if (vertexShaderError.isNotEmpty()) { // Exit on warning too
            this.errorCallback("Failed to create vertex shader for $this: $vertexShaderError")

            glDeleteShader(vertexShader)
            return OpenGLShader.INVALID_PROGRAM
        }

        val fragmentShader: Int = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentSource)
        glCompileShader(fragmentShader)
        val fragmentShaderError: String = glGetShaderInfoLog(fragmentShader)
        if (fragmentShaderError.isNotEmpty()) { // Exit on warning too
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
        if (programError.isNotEmpty()) { // Exit on warning too
            this.errorCallback("Failed to create program for $this: $programError")

            glDeleteShader(vertexShader)
            glDeleteShader(fragmentShader)
            glDeleteProgram(program)
            return OpenGLShader.INVALID_PROGRAM
        }

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
        return program
    }

    override fun computeHasInstances(): Boolean = this.dataFreeSlots.size == (this.instanceData.capacity() / this.instanceDataStrideSize)
    override fun onAddInstance(): Int {
        val i: Int? = this.dataFreeSlots.removeLastOrNull()
        if (i == null) {
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

        return i ?: ((this.instanceData.capacity() / this.instanceDataStrideSize) - 1)
    }
    override fun onRemoveInstance(i: Int): Unit {
        this.dataFreeSlots.add(i)
    }
    override fun <T> onSetVertexAttribute(
        handle: ShaderVertexAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        values: Array<T>
    ): Unit {
        val location: Int? = this.vertexAttributeMappings[handle]
        if (location == null) {
            this.errorCallback("Cannot set vertex attribute '$handle', the handle isn't registered to $this")
        } else if (!this.exists()) {
            this.errorCallback("Cannot set vertex attribute '$handle', $this doesn't exist")
        } else if (values.size != this.verticesPerInstance) {
            this.errorCallback("Cannot set vertex attribute '$handle' of $this, expected ${this.verticesPerInstance} values, got ${values.size}")
        } else {
            var i: Int = this.vertexDataStrideSize * this.verticesPerInstance * instanceI + location
            values.forEach {
                updater(this.vertexData, i, it)
                this.vertexDataDirtyRanges.add(BufferRange(i, i + handle.primitive.byteSize * handle.type.numComponents))
                i += this.vertexDataStrideSize
            }
        }
    }
    override fun <T> onSetInstanceAttribute(
        handle: ShaderInstanceAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        value: T
    ): Unit {
        val location: Int? = this.instanceAttributeMappings[handle]
        if (location == null) {
            this.errorCallback("Cannot set instance attribute '$handle', the handle isn't registered to $this")
        } else if (!this.exists()) {
            this.errorCallback("Cannot set instance attribute '$handle', $this doesn't exist")
        } else {
            val i: Int = this.instanceDataStrideSize * instanceI + location
            updater(this.instanceData, i, value)
            this.instanceDataDirtyRanges.add(BufferRange(i, i + handle.primitive.byteSize * handle.type.numComponents))
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
            this.textureBindings[unit].texture = this.samplerManager.getTexture2D(value.img)
            this.textureBindings[unit].type = GL_TEXTURE_2D
            this.textureBindings[unit].sampler = this.samplerManager.getGLSampler(value.sampler)
            glUniform1i(location, unit)
        }}
        ShaderUniformType.Image2DArraySampler -> {{
            value as ShaderSampledImage2DArray
            val unit: Int = this.textureUnitMappings[handle]!!
            this.textureBindings[unit].texture = this.samplerManager.getTexture2DArray(value.imgs)
            this.textureBindings[unit].type = GL_TEXTURE_2D_ARRAY
            this.textureBindings[unit].sampler = this.samplerManager.getGLSampler(value.sampler)
            glUniform1i(location, unit)
        }}
    }
    override fun <T> setUniform(handle: ShaderUniformHandle<T>, value: T): Unit {
        val location: Int? = this.uniformMappings[handle]
        if (location == null) {
            this.errorCallback("Cannot set uniform '$handle', the handle isn't registered to $this")
        } else if (!this.exists()) {
            this.errorCallback("Cannot set uniform '$handle', $this doesn't exist")
        } else {
            this.uniformUpdatesQueue.add(this.getUniformUpdater(handle, location, value))
        }
    }

    private fun uploadBufferRanges(data: ByteBuffer, ranges: MutableList<BufferRange>): Unit {
        ranges.sortBy { it.start }
        val mergedRanges: MutableList<BufferRange> = mutableListOf()
        var start: Int = ranges[0].start
        var end: Int = ranges[0].end
        for (i in 1..<ranges.size) {
            val range: BufferRange = ranges[i]
            if (end < range.start) {
                mergedRanges.add(BufferRange(start, end))
                start = range.start
            }
            end = range.end
        }
        mergedRanges.add(BufferRange(start, end))

        val limit: Int = data.limit()
        mergedRanges.forEach {
            data.limit(it.end).position(it.start)
            glBufferSubData(GL_ARRAY_BUFFER, it.start.toLong(), data)
        }
        data.rewind().limit(limit)
    }
    override fun onBind(): Unit {
        val context: OpenGLContext = this.contextProvider()

        glUseProgram(this.program)
        val state: OpenGLShaderState = this.stateProvider(context, this)
        glBindVertexArray(state.vao)

        if (this.vertexDataDirtyRanges.isNotEmpty()) {
            glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
            this.uploadBufferRanges(this.vertexData, this.vertexDataDirtyRanges)
            this.vertexDataDirtyRanges.clear()
        }
        if (this.instanceDataDirtyRanges.isNotEmpty()) {
            glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
            this.uploadBufferRanges(this.instanceData, this.instanceDataDirtyRanges)
            this.instanceDataDirtyRanges.clear()
        }
        if (this.uniformUpdatesQueue.isNotEmpty()) {
            this.uniformUpdatesQueue.forEach { it() }
            this.uniformUpdatesQueue.clear()
        }
        this.textureBindings.forEachIndexed { i, binding ->
            context.bindTexture(i, binding.texture, binding.type, binding.sampler)
        }
    }

    override fun onDestroy(): Unit {
        glDeleteBuffers(this.vertexBuffer)
        glDeleteBuffers(this.instanceBuffer)
        glDeleteBuffers(this.ebo)
        glDeleteProgram(this.program)
        this.program = OpenGLShader.INVALID_PROGRAM
    }
}
