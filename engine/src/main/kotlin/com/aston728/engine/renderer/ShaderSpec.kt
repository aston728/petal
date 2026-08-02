package com.aston728.engine.renderer

import com.aston728.engine.utils.NamedObject

import com.aston728.engine.internals.renderer.GLSLAttributeType

class ShaderSpec private constructor(
    private var vertexAttributes: MutableList<ShaderVertexAttributeInfo<*>>,
    private var instanceAttributes: MutableList<ShaderInstanceAttributeInfo<*>>,
    private var intermediateAttributes: MutableList<ShaderAttributeInfo>, private var outputAttributes: MutableList<ShaderAttributeInfo>,
    private var uniforms: MutableList<ShaderUniformInfo<*>>,
    private var vertexShaderBody: String, private var fragmentShaderBody: String,
) : NamedObject<ShaderSpec>("Unnamed Shader") {
    constructor() : this(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), "", "")

    companion object {
        fun merge(vararg specs: ShaderSpec): ShaderSpec {
            val vertexAttributes: MutableList<ShaderVertexAttributeInfo<*>> = mutableListOf<ShaderVertexAttributeInfo<*>>().apply {
                specs.forEach { this.addAll(it.getVertexAttributes()) }
            }
            val instanceAttributes: MutableList<ShaderInstanceAttributeInfo<*>> = mutableListOf<ShaderInstanceAttributeInfo<*>>().apply {
                specs.forEach { this.addAll(it.getInstanceAttributes()) }
            }
            val intermediateAttributes: MutableList<ShaderAttributeInfo> = mutableListOf<ShaderAttributeInfo>().apply {
                specs.forEach { this.addAll(it.getIntermediateAttributes()) }
            }
            val outputAttributes: MutableList<ShaderAttributeInfo> = mutableListOf<ShaderAttributeInfo>().apply {
                specs.forEach { this.addAll(it.getOutputAttributes()) }
            }
            val uniforms: MutableList<ShaderUniformInfo<*>> = mutableListOf<ShaderUniformInfo<*>>().apply {
                specs.forEach { this.addAll(it.getUniforms()) }
            }

            val vertexShaderBody: String = (specs.filter { !it.getVertexShaderBody().isBlank() }).last().getVertexShaderBody()
            val fragmentShaderBody: String = (specs.filter { !it.getFragmentShaderBody().isBlank() }).last().getFragmentShaderBody()

            return ShaderSpec(
                vertexAttributes, instanceAttributes, intermediateAttributes, outputAttributes, uniforms,
                vertexShaderBody, fragmentShaderBody
            ).setName(specs.last().getName())
        }
    }

    internal fun getVertexAttributes(): List<ShaderVertexAttributeInfo<*>> = this.vertexAttributes
    internal fun getInstanceAttributes(): List<ShaderInstanceAttributeInfo<*>> = this.instanceAttributes
    internal fun getIntermediateAttributes(): List<ShaderAttributeInfo> = this.intermediateAttributes
    internal fun getOutputAttributes(): List<ShaderAttributeInfo> = this.outputAttributes
    internal fun getUniforms(): List<ShaderUniformInfo<*>> = this.uniforms
    internal fun getVertexShaderBody(): String = this.vertexShaderBody
    internal fun getFragmentShaderBody(): String = this.fragmentShaderBody
    internal fun getRepresentation(contextHandle: Long): String = """
        $contextHandle${this.vertexAttributes}${this.intermediateAttributes}${this.outputAttributes}${this.uniforms}
        ${this.vertexShaderBody}
        ${this.fragmentShaderBody}
    """.trimIndent()

    // TODO: merging with same variable names
    fun <T> addVertexAttribute(name: String, handle: ShaderVertexAttributeHandle<T>): ShaderSpec = apply {
        this.vertexAttributes.add(ShaderVertexAttributeInfo(name, handle))
    }
    fun <T> addInstanceAttribute(name: String, handle: ShaderInstanceAttributeHandle<T>): ShaderSpec = apply {
        this.instanceAttributes.add(ShaderInstanceAttributeInfo(name, handle))
    }
    fun addIntermediateAttribute(name: String, type: GLSLAttributeType): ShaderSpec = apply {
        this.intermediateAttributes.add(ShaderAttributeInfo(name, type))
    }
    fun addOutputAttribute(name: String, type: GLSLAttributeType): ShaderSpec = apply {
        this.outputAttributes.add(ShaderAttributeInfo(name, type))
    }
    fun <T> addUniform(name: String, handle: ShaderUniformHandle<T>, defaultValue: T): ShaderSpec = apply {
        this.uniforms.add(ShaderUniformInfo(name, handle, defaultValue))
    }
    fun setVertexShaderSource(source: String): ShaderSpec = apply {
        this.vertexShaderBody = source
    }
    fun setFragmentShaderSource(source: String): ShaderSpec = apply {
        this.fragmentShaderBody = source
    }
}
