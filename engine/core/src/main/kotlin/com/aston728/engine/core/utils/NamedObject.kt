package com.aston728.engine.core.utils

public abstract class NamedObject<T : NamedObject<T>>(protected var _name: String) {
    protected inline fun self(block: () -> Unit): T {
        block()
        @Suppress("unchecked_cast")
        return this as T
    }

    public fun getName(): String = this._name
    public fun setName(name: String): T = this.self {
        this._name = name
    }

    override fun toString(): String = "'${this._name}'"
}
