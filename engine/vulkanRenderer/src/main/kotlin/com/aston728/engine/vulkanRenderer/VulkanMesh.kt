package com.aston728.engine.vulkanRenderer

import com.aston728.engine.core.rendererBase.IntRange
import com.aston728.engine.core.rendererBase.Mesh
import com.aston728.engine.core.rendererBase.ShaderInstanceAttributeHandle
import com.aston728.engine.core.rendererBase.ShaderVertexAttributeInfo

internal class VulkanMesh(
    name: String,
    vertexAttributes: List<ShaderVertexAttributeInfo<*>>, instanceAttributes: List<ShaderInstanceAttributeHandle<*>>,
    errorCallback: (String) -> Unit
) : Mesh(name, vertexAttributes, instanceAttributes, errorCallback) {
    override fun onAllocate(): Unit {}
    override fun onFree(): Unit {}
    override fun onAddInstance(): Unit {}
    override fun uploadVertexData(ranges: List<IntRange>): Unit {}
    override fun uploadInstanceData(ranges: List<IntRange>): Unit {}
    override fun onBind(): Unit {}
}
