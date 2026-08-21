package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.utils.NamedObject

public class ShaderSpec private constructor(
    private var vertexAttributes: MutableList<ShaderVertexAttributeInfo<*>>,
    private var instanceAttributes: MutableList<ShaderInstanceAttributeInfo<*>>,
    private var intermediateAttributes: MutableList<ShaderAttributeInfo>, private var outputAttributes: MutableList<ShaderAttributeInfo>,
    private var uniforms: MutableList<ShaderUniformInfo<*>>,
    private var vertexShaderBody: String, private var vertexShaderFooter: String,
    private var fragmentShaderBody: String, private var fragmentShaderFooter: String,
) : NamedObject<ShaderSpec>("Unnamed Shader") {
    private var isFrozen: Boolean = false
    public constructor() : this(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), "", "", "", "")

    public companion object {
        public fun merge(vararg specs: ShaderSpec): ShaderSpec {
            val vertexAttributes: MutableList<ShaderVertexAttributeInfo<*>> = mutableListOf<ShaderVertexAttributeInfo<*>>().apply {
                specs.forEach { this.addAll(it.getVertexAttributesUnsafe()) }
            }
            val instanceAttributes: MutableList<ShaderInstanceAttributeInfo<*>> = mutableListOf<ShaderInstanceAttributeInfo<*>>().apply {
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

    private fun doIfUnfrozen(actionDescription: String, block: () -> Unit): ShaderSpec {
        require(!this.isFrozen) { "$this is frozen, cannot $actionDescription" }
        block()
        return this
    }

    internal fun freeze(): Unit {
        this.isFrozen = true
    }

    internal fun getVertexAttributesUnsafe(): List<ShaderVertexAttributeInfo<*>> = this.vertexAttributes
    internal fun getInstanceAttributesUnsafe(): List<ShaderInstanceAttributeInfo<*>> = this.instanceAttributes
    internal fun getIntermediateAttributesUnsafe(): List<ShaderAttributeInfo> = this.intermediateAttributes
    internal fun getOutputAttributesUnsafe(): List<ShaderAttributeInfo> = this.outputAttributes
    internal fun getUniformsUnsafe(): List<ShaderUniformInfo<*>> = this.uniforms

    public fun getVertexAttributes(): List<ShaderVertexAttributeInfo<*>> = this.vertexAttributes.toList()
    public fun getInstanceAttributes(): List<ShaderInstanceAttributeInfo<*>> = this.instanceAttributes.toList()
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
                return "$this is invalid, there are multiple input attributes with the name '${it.name}'"
            }
            if (!vertexAttributeHandlesSeen.add(it.handle)) {
                return "$this is invalid, there are multiple vertex attributes with the same handle"
            }
        }
        val instanceAttributeHandlesSeen: HashSet<ShaderInstanceAttributeHandle<*>> = HashSet()
        this.instanceAttributes.forEach {
            if (!inputAttributeNamesSeen.add(it.name)) {
                return "$this is invalid, there are multiple input attributes with the name '${it.name}'"
            }
            if (!instanceAttributeHandlesSeen.add(it.handle)) {
                return "$this is invalid, there are multiple instance attributes with the same handle"
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
            if (!uniformNamesSeen.add(it.name)) {
                return "$this is invalid, there are multiple uniforms with the name '${it.name}'"
            }
            if (!uniformHandlesSeen.add(it.handle)) {
                return "$this is invalid, there are multiple uniforms with the same handle"
            }
        }

        return null
    }

    public fun <T> addVertexAttribute(name: String, handle: ShaderVertexAttributeHandle<T>): ShaderSpec = this.doIfUnfrozen("add vertex attribute") {
        this.vertexAttributes.add(ShaderVertexAttributeInfo(name, handle))
    }
    public fun <T> addInstanceAttribute(name: String, handle: ShaderInstanceAttributeHandle<T>): ShaderSpec = this.doIfUnfrozen("add instance attribute") {
        this.instanceAttributes.add(ShaderInstanceAttributeInfo(name, handle))
    }
    public fun addIntermediateAttribute(name: String, type: ShaderAttributeType): ShaderSpec = this.doIfUnfrozen("add intermediate attribute") {
        this.intermediateAttributes.add(ShaderAttributeInfo(name, type))
    }
    public fun addOutputAttribute(name: String, type: ShaderAttributeType): ShaderSpec = this.doIfUnfrozen("add output attribute") {
        this.outputAttributes.add(ShaderAttributeInfo(name, type))
    }
    public fun <T> addUniform(name: String, handle: ShaderUniformHandle<T>, defaultValue: T): ShaderSpec = this.doIfUnfrozen("add uniform") {
        this.uniforms.add(ShaderUniformInfo(name, handle, defaultValue))
    }
    public fun setVertexShaderBody(body: String): ShaderSpec = this.doIfUnfrozen("set vertex body") {
        this.vertexShaderBody = body
    }
    public fun addVertexFooter(footer: String): ShaderSpec = this.doIfUnfrozen("set vertex footer") {
        this.vertexShaderFooter = footer
    }
    public fun setFragmentShaderBody(body: String): ShaderSpec = this.doIfUnfrozen("set fragment body") {
        this.fragmentShaderBody = body
    }
    public fun addFragmentFooter(footer: String): ShaderSpec = this.doIfUnfrozen("set fragment footer") {
        this.fragmentShaderFooter = footer
    }
}
