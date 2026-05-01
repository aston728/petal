package com.aston728.engine.utils

abstract class NamedObject<T : NamedObject<T>> {
    protected var _name: String = "Unnamed Object"

    @Suppress("unchecked_cast")
    protected inline fun self(block: () -> Unit): T {
        block()
        return this as T
    }

    fun getName(): String = this._name
    fun setName(name: String): T = this.self {
        this._name = name
    }

    override fun toString(): String = "'${this._name}'"
}
