package com.aston728.engine.internals.core

import org.lwjgl.glfw.GLFW.glfwGetClipboardString
import org.lwjgl.glfw.GLFW.glfwSetClipboardString

import com.aston728.engine.types.ErrorHandler

class ClipboardService internal constructor(
    private val windowProvider: () -> Long? = { null },
    private val onFailure: ErrorHandler = { message -> System.err.println("[CLIPBOARD] $message") }
) {
    fun getText(): String? {
        val windowHandle: Long? = this.windowProvider()
        if (windowHandle == null) {
            this.onFailure("Cannot get text, no window available")
            return null
        }
        return glfwGetClipboardString(windowHandle)
    }
    fun setText(text: String): Unit {
        val windowHandle: Long? = this.windowProvider()
        if (windowHandle == null) {
            this.onFailure("Cannot set text, no window available")
        } else {
            glfwSetClipboardString(windowHandle, text)
        }
    }
}
