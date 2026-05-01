package com.aston728.engine.internals.core

import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback

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

    internal fun pollEvents(): Unit {
        glfwPollEvents()
    }
}
