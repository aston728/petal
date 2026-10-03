package com.aston728.engine.core.internals

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback

internal class Runtime {
    internal fun start(errorCallback: (String, Int, String) -> Unit): Boolean {
        glfwSetErrorCallback { code, info -> errorCallback("GLFW", code, GLFWErrorCallback.getDescription(info)) }
        val didSucceed: Boolean = glfwInit()
        return didSucceed
    }
    internal fun stop(): Unit {
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    internal fun setMonitorEventCallback(monitorService: MonitorService, callback: (MonitorInfo?, Boolean) -> Unit): Runtime = apply {
        glfwSetMonitorCallback { handle, action -> callback(monitorService.getInfoFromHandle(handle), action == GLFW_CONNECTED) }
    }
    internal fun pollEvents(): Unit {
        glfwPollEvents()
    }
}
