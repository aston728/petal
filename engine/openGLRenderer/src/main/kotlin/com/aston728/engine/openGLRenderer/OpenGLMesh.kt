package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.rendererBase.IntRange
import com.aston728.engine.core.rendererBase.Mesh
import com.aston728.engine.core.rendererBase.ShaderInstanceAttributeHandle
import com.aston728.engine.core.rendererBase.ShaderVertexAttributeInfo
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.opengl.GL30C.glBindVertexArray
import java.nio.ByteBuffer

internal class OpenGLMesh(
    name: String,
    vertexAttributes: List<ShaderVertexAttributeInfo<*>>, instanceAttributes: List<ShaderInstanceAttributeHandle<*>>,
    errorCallback: (String) -> Unit,
    private val contextProvider: () -> OpenGLContext, private val stateProvider: (OpenGLContext, OpenGLMesh) -> OpenGLMeshState,
) : Mesh(name, vertexAttributes, instanceAttributes, errorCallback) {
    internal var vertexBuffer: Int = -1
    internal var instanceBuffer: Int = -1
    internal var ebo: Int = -1

    internal fun getVertexAttributes(): List<ShaderVertexAttributeInfo<*>> = this._vertexAttributes
    internal fun getInstanceAttributes(): List<ShaderInstanceAttributeHandle<*>> = this._instanceAttributes
    internal fun getVertexDataStrideSize(): Int = this._vertexDataStrideSize
    internal fun getInstanceDataStrideSize(): Int = this._instanceDataStrideSize
    internal fun getInstanceData(): ByteBuffer = this._instanceData

    override fun onAllocate(): Unit {
        this.vertexBuffer = glGenBuffers()
        this.instanceBuffer = glGenBuffers()
        this.ebo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
        glBufferData(GL_ARRAY_BUFFER, this._vertexData, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intArrayOf(0, 1, 2, 1, 2, 3), GL_STATIC_DRAW)
    }
    override fun onFree() {
        glDeleteBuffers(this.vertexBuffer)
        glDeleteBuffers(this.instanceBuffer)
        glDeleteBuffers(this.ebo)
    }

    override fun onAddInstance(): Unit {
        glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
        glBufferData(GL_ARRAY_BUFFER, this._instanceData, GL_STATIC_DRAW)
    }

    override fun uploadVertexData(ranges: List<IntRange>): Unit {
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer)
        val limit: Int = this._vertexData.limit()
        ranges.forEach {
            this._vertexData.limit(it.end).position(it.start)
            glBufferSubData(GL_ARRAY_BUFFER, it.start.toLong(), this._vertexData)
        }
        this._vertexData.rewind().limit(limit)
    }
    override fun uploadInstanceData(ranges: List<IntRange>): Unit {
        glBindBuffer(GL_ARRAY_BUFFER, this.instanceBuffer)
        val limit: Int = this._instanceData.limit()
        ranges.forEach {
            this._instanceData.limit(it.end).position(it.start)
            glBufferSubData(GL_ARRAY_BUFFER, it.start.toLong(), this._instanceData)
        }
        this._instanceData.rewind().limit(limit)
    }
    override fun onBind(): Unit {
        val context: OpenGLContext = this.contextProvider()
        val state: OpenGLMeshState = this.stateProvider(context, this)
        glBindVertexArray(state.vao)
    }
}
