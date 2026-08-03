package com.aston728.engine.internals.devices

internal class KeyboardController : Keyboard {
    private val pressed: MutableList<Key> = mutableListOf()
    private val justPressed: MutableList<Key> = mutableListOf()
    private val justReleased: MutableList<Key> = mutableListOf()

    override fun isPressed(key: Key): Boolean = key in this.pressed
    override fun isJustPressed(key: Key): Boolean = key in this.justPressed
    override fun isJustReleased(key: Key): Boolean = key in this.justReleased

    internal fun cleanup(): Unit {
        this.justPressed.clear()
        this.justReleased.clear()
    }

    internal fun onPress(key: Key): Unit {
        if (key !in this.pressed) { this.pressed.add(key) }
        this.justPressed.add(key)
    }
    internal fun onRelease(key: Key): Unit {
        this.pressed.remove(key)
        this.justReleased.add(key)
    }
}
