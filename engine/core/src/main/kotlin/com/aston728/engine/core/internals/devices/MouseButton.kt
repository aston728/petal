package com.aston728.engine.core.internals.devices

import org.lwjgl.glfw.GLFW.*

public enum class MouseButton(private val button: Int) {
    LEFT(GLFW_MOUSE_BUTTON_LEFT),
    WHEEL(GLFW_MOUSE_BUTTON_MIDDLE),
    RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
    BUTTON4(GLFW_MOUSE_BUTTON_4),
    BUTTON5(GLFW_MOUSE_BUTTON_5),
    BUTTON6(GLFW_MOUSE_BUTTON_6),
    BUTTON7(GLFW_MOUSE_BUTTON_7),
    BUTTON8(GLFW_MOUSE_BUTTON_8);

    internal companion object {
        internal fun fromGLFWButton(button: Int): MouseButton =
            entries.find { it.button == button } ?:
            throw IllegalArgumentException("Invalid mouse button: $button")
    }
}
