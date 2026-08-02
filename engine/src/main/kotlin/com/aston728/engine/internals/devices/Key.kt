package com.aston728.engine.internals.devices

import org.lwjgl.glfw.GLFW.*

@JvmInline
value class Key(val code: Int) {
    init {
        require(this.code in -1..Char.MAX_VALUE.code) { "Invalid key: ${this.code}" }
    }

    companion object {
        val UNKNOWN: Key = Key(GLFW_KEY_UNKNOWN)
        val ESCAPE: Key = Key(GLFW_KEY_ESCAPE)
        val F1: Key = Key(GLFW_KEY_F1)
        val F11: Key = Key(GLFW_KEY_F11)
    }

    override fun toString(): String = "Key(code=${this.code} character=${this.code.toChar()})"
}
