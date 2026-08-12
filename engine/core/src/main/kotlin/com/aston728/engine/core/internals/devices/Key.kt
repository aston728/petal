package com.aston728.engine.core.internals.devices

import org.lwjgl.glfw.GLFW.*

@JvmInline
public value class Key(public val code: Int) {
    init {
        require(this.code in -1..Char.MAX_VALUE.code) { "Invalid key: ${this.code}" }
    }

    public companion object {
        public val UNKNOWN: Key = Key(GLFW_KEY_UNKNOWN)
        public val ESCAPE: Key = Key(GLFW_KEY_ESCAPE)
        public val F1: Key = Key(GLFW_KEY_F1)
        public val F11: Key = Key(GLFW_KEY_F11)
    }

    override fun toString(): String = "Key(code=${this.code} character=${this.code.toChar()})"
}
