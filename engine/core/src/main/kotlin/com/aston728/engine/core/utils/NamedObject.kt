package com.aston728.engine.core.utils

public abstract class NamedObject<T : NamedObject<T>>(private var name: String) {
    protected inline fun self(block: () -> Unit): T {
        block()
        @Suppress("unchecked_cast")
        return this as T
    }

    public fun getName(): String = this.name
    public fun setName(name: String): T = this.self {
        this.name = name
    }

    override fun toString(): String = "'${this.name}'"
}
