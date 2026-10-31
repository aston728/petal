package com.aston728.engine.core.internals

import org.lwjgl.glfw.GLFW.*

public enum class BackendPlatform(private val platform: Int) {
    WINDOWS(GLFW_PLATFORM_WIN32),
    MACOS(GLFW_PLATFORM_COCOA),
    WAYLAND(GLFW_PLATFORM_WAYLAND),
    X11(GLFW_PLATFORM_X11),
    UNKNOWN(GLFW_PLATFORM_NULL);

    public fun hasFullWindowControl(): Boolean = this != BackendPlatform.WAYLAND

    internal companion object {
        internal fun fromGLFWPlatform(platform: Int): BackendPlatform =
            BackendPlatform.entries.find { it.platform == platform } ?: BackendPlatform.UNKNOWN
    }
}
