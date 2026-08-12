package com.aston728.engine.core.internals.devices

public interface Keyboard {
    public fun isPressed(key: Key): Boolean
    public fun isJustPressed(key: Key): Boolean
    public fun isJustReleased(key: Key): Boolean
}
