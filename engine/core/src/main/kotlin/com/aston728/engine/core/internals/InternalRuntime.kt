package com.aston728.engine.core.internals

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback

internal class InternalRuntime {
    internal fun start(errorCallback: (Int, String) -> Unit): Boolean {
        glfwSetErrorCallback { code, info -> errorCallback(code, GLFWErrorCallback.getDescription(info)) }
        val didSucceed: Boolean = glfwInit()
        return didSucceed
    }
    internal fun stop(): Unit {
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    internal fun setMonitorEventCallback(monitorService: MonitorService, callback: (MonitorInfo?, Boolean) -> Unit): InternalRuntime = apply {
        glfwSetMonitorCallback { handle, action -> callback(monitorService.getInfoFromHandle(handle), action == GLFW_CONNECTED) }
    }
    internal fun pollEvents(): Unit {
        glfwPollEvents()
    }
}
