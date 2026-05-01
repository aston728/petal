package com.aston728.engine.internals.devices

interface Keyboard {
    fun isPressed(key: Key): Boolean
    fun isJustPressed(key: Key): Boolean
    fun isJustReleased(key: Key): Boolean
}
