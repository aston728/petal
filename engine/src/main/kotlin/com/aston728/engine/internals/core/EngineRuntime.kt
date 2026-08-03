package com.aston728.engine.internals.core

import org.lwjgl.glfw.GLFW.GLFW_CONNECTED
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwSetMonitorCallback

internal class EngineRuntime {
    internal fun start(errorCallback: (Int, String) -> Unit): Boolean {
        glfwSetErrorCallback { code, info -> errorCallback(code, GLFWErrorCallback.getDescription(info)) }
        val didSucceed: Boolean = glfwInit()
        return didSucceed
    }
    internal fun stop(): Unit {
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    internal fun setMonitorEventCallback(monitorService: MonitorService, callback: (MonitorInfo?, Boolean) -> Unit): EngineRuntime = apply {
        glfwSetMonitorCallback { handle, action -> callback(monitorService.getInfoFromHandle(handle), action == GLFW_CONNECTED)}
    }
    internal fun pollEvents(): Unit {
        glfwPollEvents()
    }
}
