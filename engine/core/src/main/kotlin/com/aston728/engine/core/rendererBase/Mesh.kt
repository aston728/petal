package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.elementBase.UIElement
import com.aston728.engine.core.utils.Handle
import com.aston728.engine.core.utils.NamedObject
import java.nio.ByteBuffer
import java.nio.ByteOrder

public data class IntRange(public val start: Int, public val end: Int)

private class ShaderVertexAttributeUpdateInfo<T>(
    val handle: ShaderVertexAttributeHandle<T>, val updater: (ByteBuffer, Int, T) -> Unit,
    val values: List<T>
)
private class ShaderInstanceAttributeUpdateInfo<T>(
    val handle: ShaderInstanceAttributeHandle<T>, val updater: (ByteBuffer, Int, T) -> Unit, val instanceI: Int,
    val value: T
)
private class Instance(size: Int) {
    val buffer: ByteBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder())
    var element: UIElement<*>? = null // TODO
    var i: Int = 0
}

public abstract class Mesh(
    name: String,
    protected val _vertexAttributes: List<ShaderVertexAttributeInfo<*>>, protected val _instanceAttributes: List<ShaderInstanceAttributeHandle<*>>,
    private val errorCallback: (String) -> Unit
) : NamedObject<Mesh>(name) {
    protected val _vertexDataStrideSize: Int = this._vertexAttributes.sumOf { it.handle.primitive.byteSize * it.handle.type.numComponents }
    protected val _verticesPerInstance: Int = 4
    protected val _vertexData: ByteBuffer = ByteBuffer
        .allocateDirect(this._vertexDataStrideSize * this._verticesPerInstance)
        .order(ByteOrder.nativeOrder())

    private val iData: MutableList<Instance> = mutableListOf()
    protected val _instanceDataStrideSize: Int = this._instanceAttributes.sumOf { it.primitive.byteSize * it.type.numComponents }
    protected var _instanceData: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
        private set

    private val vertexAttributeMappings: Map<ShaderVertexAttributeHandle<*>, Int> = this.getVertexAttributeMappings()
    private val instanceAttributeMappings: Map<ShaderInstanceAttributeHandle<*>, Int> = this.getInstanceAttributeMappings()

    private var dataFreeSlots: MutableList<Int> = mutableListOf()
    private var vertexAttributesUpdateQueue: MutableList<ShaderVertexAttributeUpdateInfo<*>> = mutableListOf()
    private var instanceAttributesUpdateQueue: MutableList<ShaderInstanceAttributeUpdateInfo<*>> = mutableListOf()
    private var isFreed: Boolean = true

    private val onDestroyHandlers: MutableList<(Mesh) -> Unit> = mutableListOf()

    private fun getVertexAttributeMappings(): Map<ShaderVertexAttributeHandle<*>, Int> {
        var vertexAttributeLocation: Int = 0
        return this._vertexAttributes.associate {
            val location: Int = vertexAttributeLocation
            vertexAttributeLocation += it.handle.primitive.byteSize * it.handle.type.numComponents
            it.handle to location
        }
    }
    private fun getInstanceAttributeMappings(): Map<ShaderInstanceAttributeHandle<*>, Int> {
        var instanceAttributeLocation: Int = 0
        return this._instanceAttributes.associateWith {
            val location: Int = instanceAttributeLocation
            instanceAttributeLocation += it.primitive.byteSize * it.type.numComponents
            location
        }
    }

    internal fun getVertexAttributes(): List<ShaderVertexAttributeInfo<*>> = this._vertexAttributes

    public fun addOnDestroyHandler(handler: (Mesh) -> Unit, handle: Handle? = null): Unit {
        val wrapper: (Mesh) -> Unit = { mesh -> handler(mesh) }
        this.onDestroyHandlers.add(wrapper)
        handle?.setOnRemoveHandler { this.onDestroyHandlers.remove(wrapper) }
    }

    protected abstract fun onAllocate(): Unit
    protected abstract fun onFree(): Unit
    protected abstract fun onAddInstance(): Unit
    protected abstract fun uploadVertexData(ranges: List<IntRange>): Unit
    protected abstract fun uploadInstanceData(ranges: List<IntRange>): Unit
    protected abstract fun onBind(): Unit

    internal fun allocate(): Unit {
        if (this.isFreed) {
            this.onAllocate()
            this.isFreed = false
        }
    }
    internal fun free(): Unit {
        this.onFree()
        this.isFreed = true
        this.onDestroyHandlers.toList().forEach { it(this) }
    }

    internal fun hasInstances(): Boolean = this.iData.size == this.dataFreeSlots.size
    internal fun addInstance(element: UIElement<*>): Int {
        val i: Int? = this.dataFreeSlots.removeLastOrNull()
        if (i == null) {
            val iData: Instance = Instance(this._instanceDataStrideSize)
            this.iData.add(iData)
            this._instanceData = ByteBuffer
                .allocateDirect(this._instanceData.capacity() + this._instanceDataStrideSize)
                .order(ByteOrder.nativeOrder())
                .put(this._instanceData)
                .rewind()
            this.onAddInstance()
        }

        val instanceI: Int = i ?: (this.iData.size - 1)
        this.iData[instanceI].element = element
        this.iData[instanceI].i = instanceI
        return instanceI
    }
    internal fun removeInstance(i: Int): Unit {
        this.dataFreeSlots.add(i)
    }

    internal fun <T> setVertexAttribute(
        handle: ShaderVertexAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit,
        values: List<T>
    ): Unit {
        this.vertexAttributesUpdateQueue.add(ShaderVertexAttributeUpdateInfo(handle, updater, values))
    }
    internal fun <T> setInstanceAttribute(
        handle: ShaderInstanceAttributeHandle<T>, updater: (ByteBuffer, Int, T) -> Unit, instanceI: Int,
        value: T
    ): Unit {
        this.instanceAttributesUpdateQueue.add(ShaderInstanceAttributeUpdateInfo(handle, updater, instanceI, value))
    }

    internal fun sortData(allElements: List<List<UIElement<*>>>): Unit {
        val newInstanceData: ByteBuffer = ByteBuffer
            .allocateDirect(this._instanceDataStrideSize * allElements.sumOf { it.size })
            .order(ByteOrder.nativeOrder())
        var i: Int = 0
        for (elements in allElements) {
            for (element in elements) {
                val instance: Instance = this.iData.first { it.element === element }
                this._instanceData.limit((instance.i + 1) * this._instanceDataStrideSize)
                this._instanceData.position(instance.i * this._instanceDataStrideSize)
                newInstanceData.put(this._instanceData)
                instance.i = i
                i++
            }
        }
        this._instanceData = newInstanceData.rewind()
    }

    private fun getMergedRanges(ranges: MutableList<IntRange>): List<IntRange> {
        ranges.sortBy { it.start }
        val mergedRanges: MutableList<IntRange> = mutableListOf()
        var start: Int = ranges[0].start
        var end: Int = ranges[0].end
        for (i in 1..<ranges.size) {
            val range: IntRange = ranges[i]
            if (end < range.start) {
                mergedRanges.add(IntRange(start, end))
                start = range.start
            }
            end = range.end
        }
        mergedRanges.add(IntRange(start, end))

        return mergedRanges
    }
    private fun <T> updateVertexAttribute(info: ShaderVertexAttributeUpdateInfo<T>, dirtyRanges: MutableList<IntRange>): Unit {
        val location: Int? = this.vertexAttributeMappings[info.handle]
        if (location == null) {
            this.errorCallback("Cannot set vertex attribute '${info.handle}', the handle isn't registered to $this")
        } else if (this._verticesPerInstance != info.values.size) {
            this.errorCallback("Cannot set vertex attribute '${info.handle}' of $this, expected ${this._verticesPerInstance} values, got ${info.values.size}")
        } else {
            var i: Int = location
            info.values.forEach {
                info.updater(this._vertexData, i, it)
                dirtyRanges.add(IntRange(i, i + info.handle.primitive.byteSize * info.handle.type.numComponents))
                i += this._vertexDataStrideSize
            }
        }
    }
    private fun <T> updateInstanceAttribute(info: ShaderInstanceAttributeUpdateInfo<T>, dirtyRanges: MutableList<IntRange>): Unit {
        val location: Int? = this.instanceAttributeMappings[info.handle]
        if (location == null) {
            this.errorCallback("Cannot set instance attribute '${info.handle}', the handle isn't registered to $this")
        } else {
            val instance: Instance = this.iData[info.instanceI]
            val i: Int = this._instanceDataStrideSize * instance.i + location
            info.updater(this._instanceData, i, info.value)
            dirtyRanges.add(IntRange(i, i + info.handle.primitive.byteSize * info.handle.type.numComponents))
        }
    }
    internal fun bind(): Unit {
        this.onBind()

        if (this.vertexAttributesUpdateQueue.isNotEmpty()) {
            val dirtyRanges: MutableList<IntRange> = mutableListOf()
            this.vertexAttributesUpdateQueue.forEach { this.updateVertexAttribute(it, dirtyRanges) }
            val mergedRanges: List<IntRange> = this.getMergedRanges(dirtyRanges)
            this.uploadVertexData(mergedRanges)
            this.vertexAttributesUpdateQueue.clear()
        }

        if (this.instanceAttributesUpdateQueue.isNotEmpty()) {
            val dirtyRanges: MutableList<IntRange> = mutableListOf()
            this.instanceAttributesUpdateQueue.forEach { this.updateInstanceAttribute(it, dirtyRanges) }
            val mergedRanges: List<IntRange> = this.getMergedRanges(dirtyRanges)
            this.uploadInstanceData(mergedRanges)
            this.instanceAttributesUpdateQueue.clear()
        }
    }
}
