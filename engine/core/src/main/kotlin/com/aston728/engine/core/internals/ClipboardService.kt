package com.aston728.engine.core.internals

import org.lwjgl.glfw.GLFW.glfwGetClipboardString
import org.lwjgl.glfw.GLFW.glfwSetClipboardString

public class ClipboardService internal constructor(
    private val windowProvider: () -> InternalWindow? = { null },
    private val errorCallback: (String) -> Unit = { message -> System.err.println("[CLIPBOARD] $message") }
) {
    public fun getText(): String? {
        val windowHandle: Long? = this.windowProvider()?.getHandle()
        if (windowHandle == null) {
            this.errorCallback("Cannot get clipboard text, no window available")
            return null
        }
        return glfwGetClipboardString(windowHandle)
    }
    public fun setText(text: String): Unit {
        val windowHandle: Long? = this.windowProvider()?.getHandle()
        if (windowHandle == null) {
            this.errorCallback("Cannot set clipboard text, no window available")
        } else {
            glfwSetClipboardString(windowHandle, text)
        }
    }
}
