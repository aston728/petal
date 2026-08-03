package com.aston728.engine.internals.renderer

import org.lwjgl.opengl.GL30C.*

import com.aston728.engine.renderer.Shader
import com.aston728.engine.renderer.ShaderInstanceAttributeHandle
import com.aston728.engine.renderer.ShaderInstanceAttributeInfo
import com.aston728.engine.renderer.ShaderUniformHandle
import com.aston728.engine.renderer.ShaderVertexAttributeInfo
import com.aston728.engine.renderer.ShaderUniformInfo
import com.aston728.engine.renderer.ShaderVertexAttributeHandle

import com.aston728.engine.types.Handler
import com.aston728.engine.types.ErrorHandler
import kotlin.collections.Map

private class ShaderAttributeData(val name: String, val location: Int)

internal class OpenGLShader internal constructor(
    name: String,
    vertexAttributesInfo: List<ShaderVertexAttributeBinding>, private val vertexDataStrideSize: Int, private val verticesPerInstance: Int,
    instanceAttributesInfo: List<ShaderInstanceAttributeBinding>, private val instanceDataStrideSize: Int,
    uniformsInfo: List<ShaderUniformInfo<*>>,
    vertexSource: String, fragmentSource: String,
    private val errorCallback: ErrorHandler,
) : Shader(name) {
    private companion object {
        private const val INVALID_PROGRAM: Int = -1
    }

    private var vertexData: FloatArray = floatArrayOf()
    private var instanceData: FloatArray = floatArrayOf()
    private var isVertexDataDirty: Boolean = false
    private var isInstanceDataDirty: Boolean = false
    private val uniformUpdatesQueue: MutableList<Handler> = mutableListOf()

    private val vao: Int = glGenVertexArrays()
    private val vertexBuffer: Int = glGenBuffers()
    private val instanceBuffer: Int = glGenBuffers()
    private val ebo: Int = glGenBuffers()
    private var program: Int = this.createProgram(vertexSource, fragmentSource)

    private val vertexAttributeMappings: Map<ShaderVertexAttributeHandle<*>, ShaderAttributeData> = vertexAttributesInfo.associate {
        it.handle to ShaderAttributeData(it.name, it.bufferLocation)
    }
    private val instanceAttributeMappings: Map<ShaderInstanceAttributeHandle<*>, ShaderAttributeData> = instanceAttributesInfo.associate {
        it.handle to ShaderAttributeData(it.name, it.bufferLocation)
    }
    private val uniformMappings: Map<ShaderUniformHandle<*>, ShaderAttributeData> =
        if (this.isValid()) {
            uniformsInfo.associate { it.handle to ShaderAttributeData(it.name, glGetUniformLocation(this.program, it.name)) }
        } else { emptyMap() }

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
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intArrayOf(0, 1, 2, 1, 2, 3, 4, 5, 6, 5, 6, 7), GL_STATIC_DRAW)

        return program
    }

    override fun isValid(): Boolean = this.program != OpenGLShader.INVALID_PROGRAM

    override fun addInstance(): Unit {
        glUseProgram(this.program)
        glBindVertexArray(this.vao)

        this.vertexData += FloatArray(this.vertexDataStrideSize * this.verticesPerInstance)
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
        glBufferData(GL_ARRAY_BUFFER, this.vertexData, GL_STATIC_DRAW)

        this.instanceData += FloatArray(this.instanceDataStrideSize)
        glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
        glBufferData(GL_ARRAY_BUFFER, this.instanceData, GL_STATIC_DRAW)
    }
    override fun removeInstance(i: Int): Unit {
        glUseProgram(this.program)
        glBindVertexArray(this.vao)

        val vertexStartI: Int = this.vertexDataStrideSize * this.verticesPerInstance * i
        val vertexEndI: Int = vertexStartI + this.vertexDataStrideSize * this.verticesPerInstance
        this.vertexData =
            this.vertexData.sliceArray(0 until vertexStartI) +
            this.vertexData.sliceArray(vertexEndI + 1 until this.vertexData.size)
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
        glBufferData(GL_ARRAY_BUFFER, this.vertexData, GL_STATIC_DRAW)

        val instanceStartI: Int = this.instanceDataStrideSize * i
        val instanceEndI: Int = instanceStartI + this.instanceDataStrideSize
        this.instanceData =
            this.instanceData.sliceArray(0 until instanceStartI) +
            this.instanceData.sliceArray(instanceEndI + 1 until this.instanceData.size)
        glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
        glBufferData(GL_ARRAY_BUFFER, this.instanceData, GL_STATIC_DRAW)
    }
    override fun <T> setVertexAttribute(handle: ShaderVertexAttributeHandle<T>, instanceI: Int, values: Array<T>) {
        val data: ShaderAttributeData? = this.vertexAttributeMappings[handle]
        if (data == null) {
            this.errorCallback("Cannot set vertex attribute, this handle isn't registered to $this")
        } else if (!this.isValid()) {
            this.errorCallback("Cannot set vertex attribute '${data.name}', $this is invalid")
        } else if (values.size != this.verticesPerInstance) {
            this.errorCallback("Cannot set vertex attribute '${data.name}' of $this, expected ${this.verticesPerInstance} values, got ${values.size}")
        } else {
            var i: Int = this.vertexDataStrideSize * this.verticesPerInstance * instanceI + data.location
            values.forEach {
                handle.descriptor.updater(this.vertexData, i, it)
                i += this.vertexDataStrideSize
            }
            this.isVertexDataDirty = true
        }
    }
    override fun <T> setInstanceAttribute(handle: ShaderInstanceAttributeHandle<T>, instanceI: Int, value: T) {
        val data: ShaderAttributeData? = this.instanceAttributeMappings[handle]
        if (data == null) {
            this.errorCallback("Cannot set instance attribute, this handle isn't registered to $this")
        } else if (!this.isValid()) {
            this.errorCallback("Cannot set instance attribute '${data.name}', $this is invalid")
        } else {
            val i: Int = this.instanceDataStrideSize * instanceI + data.location
            handle.descriptor.updater(this.instanceData, i, value)
            this.isInstanceDataDirty = true
        }
    }
    override fun <T> setUniform(handle: ShaderUniformHandle<T>, value: T) {
        val data: ShaderAttributeData? = this.uniformMappings[handle]
        if (data == null) {
            this.errorCallback("Cannot set uniform, this handle isn't registered to $this")
        } else if (!this.isValid()) {
            this.errorCallback("Cannot set uniform '${data.name}', $this is invalid")
        } else {
            this.uniformUpdatesQueue.add { handle.descriptor.updater(data.location, value) }
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
    override fun destroy(): Unit {
        glDeleteVertexArrays(this.vao)
        glDeleteBuffers(this.vertexBuffer)
        glDeleteBuffers(this.instanceBuffer)
        glDeleteBuffers(this.ebo)
        glDeleteProgram(this.program)
        this.program = OpenGLShader.INVALID_PROGRAM
    }
    override fun draw() { // TODO
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

        glDrawElements(GL_TRIANGLES, 12, GL_UNSIGNED_INT, 0)
    }
}
