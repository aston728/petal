package com.aston728.engine.core.internals

import com.aston728.engine.core.types.ErrorHandler
import org.lwjgl.glfw.GLFW.glfwGetClipboardString
import org.lwjgl.glfw.GLFW.glfwSetClipboardString

public class ClipboardService internal constructor(
    private val windowProvider: () -> Long? = { null },
    private val errorCallback: ErrorHandler = { message -> System.err.println("[CLIPBOARD] $message") }
) {
    public fun getText(): String? {
        val windowHandle: Long? = this.windowProvider()
        if (windowHandle == null) {
            this.errorCallback("Cannot get text, no window available")
            return null
        }
        return glfwGetClipboardString(windowHandle)
    }
    public fun setText(text: String): Unit {
        val windowHandle: Long? = this.windowProvider()
        if (windowHandle == null) {
            this.errorCallback("Cannot set text, no window available")
        } else {
            glfwSetClipboardString(windowHandle, text)
        }
    }
}
