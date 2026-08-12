package com.aston728.engine.core.internals

import org.lwjgl.glfw.GLFW.glfwGetTime

public class TimeService internal constructor() {
    public fun getSecondsSinceStart(): Double = glfwGetTime()
}
