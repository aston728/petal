package com.aston728.engine.internals.devices

import org.lwjgl.glfw.GLFW.*

enum class DeviceAction(private val action: Int) {
    PRESSED(GLFW_PRESS),
    RELEASE(GLFW_RELEASE),
    REPEAT(GLFW_REPEAT);

    companion object {
        internal fun fromInt(action: Int): DeviceAction =
            entries.find { it.action == action } ?:
            throw IllegalArgumentException("Invalid device action: $action")
    }
}
