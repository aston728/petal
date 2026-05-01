package com.aston728.engine.internals.core

import org.lwjgl.glfw.GLFW.glfwGetTime

class TimeService internal constructor() {
    fun getSecondsSinceStart(): Double = glfwGetTime()
}
