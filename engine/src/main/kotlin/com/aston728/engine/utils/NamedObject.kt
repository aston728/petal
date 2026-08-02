package com.aston728.engine.utils

import com.aston728.engine.types.Handler

abstract class NamedObject<T : NamedObject<T>>(protected var _name: String) {
    protected inline fun self(block: Handler): T {
        block()
        @Suppress("unchecked_cast")
        return this as T
    }

    fun getName(): String = this._name
    fun setName(name: String): T = this.self {
        this._name = name
    }

    override fun toString(): String = "'${this._name}'"
}
