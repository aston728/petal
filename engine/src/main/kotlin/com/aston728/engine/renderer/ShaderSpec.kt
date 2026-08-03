package com.aston728.engine.renderer

import com.aston728.engine.core.Window
import com.aston728.engine.core.WindowState
import com.aston728.engine.utils.NamedObject

import com.aston728.engine.internals.renderer.GLSLAttributeType
import com.aston728.engine.types.ErrorHandler
import com.aston728.engine.types.Handler

class ShaderSpec private constructor(
    private var vertexAttributes: MutableList<ShaderVertexAttributeInfo<*>>,
    private var instanceAttributes: MutableList<ShaderInstanceAttributeInfo<*>>,
    private var intermediateAttributes: MutableList<ShaderAttributeInfo>, private var outputAttributes: MutableList<ShaderAttributeInfo>,
    private var uniforms: MutableList<ShaderUniformInfo<*>>,
    private var vertexShaderBody: String, private var fragmentShaderBody: String,
) : NamedObject<ShaderSpec>("Unnamed Shader") {
    private var isFrozen: Boolean = true
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

            val vertexShaderBody: String = specs
                .findLast { it.getVertexShaderBody().isNotBlank() }?.getVertexShaderBody()
                ?: ""
            val fragmentShaderBody: String = specs
                .findLast { it.getFragmentShaderBody().isNotBlank() }?.getFragmentShaderBody()
                ?: ""

            return ShaderSpec(
                vertexAttributes, instanceAttributes, intermediateAttributes, outputAttributes, uniforms,
                vertexShaderBody, fragmentShaderBody
            ).setName(specs.last().getName())
        }
    }

    private fun doIfUnfrozen(actionDescription: String, block: Handler): ShaderSpec {
        require(!this.isFrozen) { "$this is frozen, cannot $actionDescription" }
        block()
        return this
    }

    internal fun freeze(): Unit {
        this.isFrozen = true
    }
    internal fun checkValidity(errorCallback: ErrorHandler): Boolean {
        val inputAttributeNamesSeen: HashSet<String> = HashSet()
        val vertexAttributeHandlesSeen: HashSet<ShaderVertexAttributeHandle<*>> = HashSet()
        this.vertexAttributes.forEach {
            if (!inputAttributeNamesSeen.add(it.name)) {
                errorCallback("$this is invalid, there are multiple input attributes with the name '${it.name}'")
                return false
            }
            if (!vertexAttributeHandlesSeen.add(it.handle)) {
                errorCallback("$this is invalid, there are multiple vertex attributes with the same handle")
                return false
            }
        }
        val instanceAttributeHandlesSeen: HashSet<ShaderInstanceAttributeHandle<*>> = HashSet()
        this.instanceAttributes.forEach {
            if (!inputAttributeNamesSeen.add(it.name)) {
                errorCallback("$this is invalid, there are multiple input attributes with the name '${it.name}'")
                return false
            }
            if (!instanceAttributeHandlesSeen.add(it.handle)) {
                errorCallback("$this is invalid, there are multiple instance attributes with the same handle")
                return false
            }
        }

        val intermediateAttributeNamesSeen: HashSet<String> = HashSet()
        this.intermediateAttributes.forEach {
            if (!intermediateAttributeNamesSeen.add(it.name)) {
                errorCallback("$this is invalid, there are multiple intermediate attributes with the name '${it.name}'")
                return false
            }
        }

        val outputAttributeNamesSeen: HashSet<String> = HashSet()
        this.outputAttributes.forEach {
            if (!outputAttributeNamesSeen.add(it.name)) {
                errorCallback("$this is invalid, there are multiple output attributes with the name '${it.name}'")
                return false
            }
        }

        val uniformNamesSeen: HashSet<String> = HashSet()
        val uniformHandlesSeen: HashSet<ShaderUniformHandle<*>> = HashSet()
        this.uniforms.forEach {
            if (!uniformNamesSeen.add(it.name)) {
                errorCallback("$this is invalid, there are multiple uniforms with the name '${it.name}'")
                return false
            }
            if (!uniformHandlesSeen.add(it.handle)) {
                errorCallback("$this is invalid, there are multiple uniforms with the same handle")
                return false
            }
        }

        return true
    }

    internal fun getVertexAttributes(): List<ShaderVertexAttributeInfo<*>> = this.vertexAttributes
    internal fun getInstanceAttributes(): List<ShaderInstanceAttributeInfo<*>> = this.instanceAttributes
    internal fun getIntermediateAttributes(): List<ShaderAttributeInfo> = this.intermediateAttributes
    internal fun getOutputAttributes(): List<ShaderAttributeInfo> = this.outputAttributes
    internal fun getUniforms(): List<ShaderUniformInfo<*>> = this.uniforms
    internal fun getVertexShaderBody(): String = this.vertexShaderBody
    internal fun getFragmentShaderBody(): String = this.fragmentShaderBody
    internal fun getRepresentation(): String = """
        ${this.vertexAttributes}${this.intermediateAttributes}${this.outputAttributes}${this.uniforms}
        ${this.vertexShaderBody}
        ${this.fragmentShaderBody}
    """.trimIndent()

    fun <T> addVertexAttribute(name: String, handle: ShaderVertexAttributeHandle<T>): ShaderSpec = this.doIfUnfrozen("add vertex attribute") {
        this.vertexAttributes.add(ShaderVertexAttributeInfo(name, handle))
    }
    fun <T> addInstanceAttribute(name: String, handle: ShaderInstanceAttributeHandle<T>): ShaderSpec = this.doIfUnfrozen("add instance attribute") {
        this.instanceAttributes.add(ShaderInstanceAttributeInfo(name, handle))
    }
    fun addIntermediateAttribute(name: String, type: GLSLAttributeType): ShaderSpec = this.doIfUnfrozen("add intermediate attribute") {
        this.intermediateAttributes.add(ShaderAttributeInfo(name, type))
    }
    fun addOutputAttribute(name: String, type: GLSLAttributeType): ShaderSpec = this.doIfUnfrozen("add output attribute") {
        this.outputAttributes.add(ShaderAttributeInfo(name, type))
    }
    fun <T> addUniform(name: String, handle: ShaderUniformHandle<T>, defaultValue: T): ShaderSpec = this.doIfUnfrozen("add uniform") {
        this.uniforms.add(ShaderUniformInfo(name, handle, defaultValue))
    }
    fun setVertexShaderSource(source: String): ShaderSpec = this.doIfUnfrozen("set vertex source") {
        this.vertexShaderBody = source
    }
    fun setFragmentShaderSource(source: String): ShaderSpec = this.doIfUnfrozen("set fragment source") {
        this.fragmentShaderBody = source
    }
}
