package com.aston728.engine.core.internals

import org.lwjgl.glfw.GLFW.glfwGetPlatform
import org.lwjgl.glfw.GLFW.glfwGetVersionString

public class BackendInfo internal constructor() {
    public fun getPlatform(): BackendPlatform = BackendPlatform.fromGLFWPlatform(glfwGetPlatform())
    public fun getVersion(): String = glfwGetVersionString()
}
