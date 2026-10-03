package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.NamedObject

public class ShaderSpec private constructor(
    private var vertexAttributes: MutableList<ShaderVertexAttributeHandle<*>>,
    private var instanceAttributes: MutableList<ShaderInstanceAttributeHandle<*>>,
    private var intermediateAttributes: MutableList<ShaderAttributeInfo>, private var outputAttributes: MutableList<ShaderAttributeInfo>,
    private var uniforms: MutableList<ShaderUniformInfo<*>>,
    private var vertexShaderBody: String, private var vertexShaderFooter: String,
    private var fragmentShaderBody: String, private var fragmentShaderFooter: String,
) : NamedObject<ShaderSpec>("Unnamed Shader") {
    public constructor() : this(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), "", "", "", "")

    public companion object {
        public fun merge(vararg specs: ShaderSpec): ShaderSpec {
            val vertexAttributes: MutableList<ShaderVertexAttributeHandle<*>> = mutableListOf<ShaderVertexAttributeHandle<*>>().apply {
                specs.forEach { this.addAll(it.getVertexAttributesUnsafe()) }
            }
            val instanceAttributes: MutableList<ShaderInstanceAttributeHandle<*>> = mutableListOf<ShaderInstanceAttributeHandle<*>>().apply {
                specs.forEach { this.addAll(it.getInstanceAttributesUnsafe()) }
            }
            val intermediateAttributes: MutableList<ShaderAttributeInfo> = mutableListOf<ShaderAttributeInfo>().apply {
                specs.forEach { this.addAll(it.getIntermediateAttributesUnsafe()) }
            }
            val outputAttributes: MutableList<ShaderAttributeInfo> = mutableListOf<ShaderAttributeInfo>().apply {
                specs.forEach { this.addAll(it.getOutputAttributesUnsafe()) }
            }
            val uniforms: MutableList<ShaderUniformInfo<*>> = mutableListOf<ShaderUniformInfo<*>>().apply {
                specs.forEach { this.addAll(it.getUniformsUnsafe()) }
            }

            val vertexShaderBody: String = specs
                .findLast { it.getVertexShaderBody().isNotBlank() }?.getVertexShaderBody()
                ?: ""
            val vertexShaderFooter: String = specs.asList().asReversed().joinToString("\n") { it.getVertexShaderFooter() }
            val fragmentShaderBody: String = specs
                .findLast { it.getFragmentShaderBody().isNotBlank() }?.getFragmentShaderBody()
                ?: ""
            val fragmentShaderFooter: String = specs.asList().asReversed().joinToString("\n") { it.getFragmentShaderFooter() }

            return ShaderSpec(
                vertexAttributes, instanceAttributes, intermediateAttributes, outputAttributes, uniforms,
                vertexShaderBody, vertexShaderFooter, fragmentShaderBody, fragmentShaderFooter
            ).setName(specs.last()._name)
        }
    }

    internal fun getVertexAttributesUnsafe(): List<ShaderVertexAttributeHandle<*>> = this.vertexAttributes
    internal fun getInstanceAttributesUnsafe(): List<ShaderInstanceAttributeHandle<*>> = this.instanceAttributes
    internal fun getIntermediateAttributesUnsafe(): List<ShaderAttributeInfo> = this.intermediateAttributes
    internal fun getOutputAttributesUnsafe(): List<ShaderAttributeInfo> = this.outputAttributes
    internal fun getUniformsUnsafe(): List<ShaderUniformInfo<*>> = this.uniforms

    public fun getVertexAttributes(): List<ShaderVertexAttributeHandle<*>> = this.vertexAttributes.toList()
    public fun getInstanceAttributes(): List<ShaderInstanceAttributeHandle<*>> = this.instanceAttributes.toList()
    public fun getIntermediateAttributes(): List<ShaderAttributeInfo> = this.intermediateAttributes.toList()
    public fun getOutputAttributes(): List<ShaderAttributeInfo> = this.outputAttributes.toList()
    public fun getUniforms(): List<ShaderUniformInfo<*>> = this.uniforms.toList()
    public fun getVertexShaderBody(): String = this.vertexShaderBody
    public fun getVertexShaderFooter(): String = this.vertexShaderFooter
    public fun getFragmentShaderBody(): String = this.fragmentShaderBody
    public fun getFragmentShaderFooter(): String = this.fragmentShaderFooter
    public fun getRepresentation(): String = """
        ${this.vertexAttributes}${this.intermediateAttributes}${this.outputAttributes}${this.uniforms}
        ${this.vertexShaderBody}
        ${this.vertexShaderFooter}
        ${this.fragmentShaderBody}
        ${this.fragmentShaderFooter}
    """.trimIndent()
    public fun checkValidity(): String? {
        val inputAttributeNamesSeen: HashSet<String> = HashSet()
        val vertexAttributeHandlesSeen: HashSet<ShaderVertexAttributeHandle<*>> = HashSet()
        this.vertexAttributes.forEach {
            if (!inputAttributeNamesSeen.add(it.name)) {
                return "$this is invalid, there are multiple input attributes with the name $it"
            }
            if (!vertexAttributeHandlesSeen.add(it)) {
                return "$this is invalid, there are multiple vertex attributes with the handle $it"
            }
        }
        val instanceAttributeHandlesSeen: HashSet<ShaderInstanceAttributeHandle<*>> = HashSet()
        this.instanceAttributes.forEach {
            if (!inputAttributeNamesSeen.add(it.name)) {
                return "$this is invalid, there are multiple input attributes with the name $it"
            }
            if (!instanceAttributeHandlesSeen.add(it)) {
                return "$this is invalid, there are multiple instance attributes with the handle $it"
            }
        }

        val intermediateAttributeNamesSeen: HashSet<String> = HashSet()
        this.intermediateAttributes.forEach {
            if (!intermediateAttributeNamesSeen.add(it.name)) {
                return "$this is invalid, there are multiple intermediate attributes with the name '${it.name}'"
            }
        }

        val outputAttributeNamesSeen: HashSet<String> = HashSet()
        this.outputAttributes.forEach {
            if (!outputAttributeNamesSeen.add(it.name)) {
                return "$this is invalid, there are multiple output attributes with the name '${it.name}'"
            }
        }

        val uniformNamesSeen: HashSet<String> = HashSet()
        val uniformHandlesSeen: HashSet<ShaderUniformHandle<*>> = HashSet()
        this.uniforms.forEach {
            if (!uniformNamesSeen.add(it.handle.name)) {
                return "$this is invalid, there are multiple uniforms with the name ${it.handle}"
            }
            if (!uniformHandlesSeen.add(it.handle)) {
                return "$this is invalid, there are multiple uniforms with the handle ${it.handle}"
            }
        }

        return null
    }

    public fun <T> addVertexAttribute(handle: ShaderVertexAttributeHandle<T>): ShaderSpec = apply {
        this.vertexAttributes.add(handle)
    }
    public fun <T> addInstanceAttribute(handle: ShaderInstanceAttributeHandle<T>): ShaderSpec = apply {
        this.instanceAttributes.add(handle)
    }
    public fun addIntermediateAttribute(name: String, type: ShaderAttributeType): ShaderSpec = apply {
        this.intermediateAttributes.add(ShaderAttributeInfo(name, type))
    }
    public fun addOutputAttribute(name: String, type: ShaderAttributeType): ShaderSpec = apply {
        this.outputAttributes.add(ShaderAttributeInfo(name, type))
    }
    public fun <T> addUniform(handle: ShaderUniformHandle<T>, defaultValue: T): ShaderSpec = apply {
        this.uniforms.add(ShaderUniformInfo(handle, defaultValue))
    }
    public fun setVertexShaderBody(body: String): ShaderSpec = apply {
        this.vertexShaderBody = body
    }
    public fun addVertexFooter(footer: String): ShaderSpec = apply {
        this.vertexShaderFooter = footer
    }
    public fun setFragmentShaderBody(body: String): ShaderSpec = apply {
        this.fragmentShaderBody = body
    }
    public fun addFragmentFooter(footer: String): ShaderSpec = apply {
        this.fragmentShaderFooter = footer
    }
}
