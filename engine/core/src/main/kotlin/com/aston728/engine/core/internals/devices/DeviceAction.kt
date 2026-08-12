package com.aston728.engine.core.internals.devices

import org.lwjgl.glfw.GLFW.*

public enum class DeviceAction(private val action: Int) {
    PRESSED(GLFW_PRESS),
    RELEASE(GLFW_RELEASE),
    REPEAT(GLFW_REPEAT);

    internal companion object {
        internal fun fromGLFWAction(action: Int): DeviceAction =
            entries.find { it.action == action } ?:
            throw IllegalArgumentException("Invalid device action: $action")
    }
}
