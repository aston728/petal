package com.aston728.engine.internals.core

import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.glfw.GLFW.glfwGetError
import org.lwjgl.glfw.GLFW.GLFW_NO_ERROR
import org.lwjgl.glfw.GLFW.glfwGetMonitors
import org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor
import org.lwjgl.glfw.GLFW.glfwGetMonitorName
import org.lwjgl.glfw.GLFW.glfwGetMonitorPos
import org.lwjgl.glfw.GLFW.glfwGetMonitorWorkarea
import org.lwjgl.glfw.GLFW.glfwGetVideoMode

import com.aston728.engine.layout.Rect

import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.IntSize

class MonitorService internal constructor() {
    private fun getInfoFromHandle(handle: Long): MonitorInfo? {
        glfwGetError(null) // clear previous error

        val name: String = glfwGetMonitorName(handle) ?: "Unknown Monitor"

        val x: IntArray = IntArray(1)
        val y: IntArray = IntArray(1)
        glfwGetMonitorPos(handle, x, y)
        if (glfwGetError(null) != GLFW_NO_ERROR) { return null }

        val videoMode: GLFWVidMode = glfwGetVideoMode(handle) ?: return null

        val usableX: IntArray = IntArray(1)
        val usableY: IntArray = IntArray(1)
        val usableWidth: IntArray = IntArray(1)
        val usableHeight: IntArray = IntArray(1)
        glfwGetMonitorWorkarea(handle, usableX, usableY, usableWidth, usableHeight)
        if (glfwGetError(null) != GLFW_NO_ERROR) { return null }

        return MonitorInfo(
            handle, name,
            Rect(IntPosition(x[0], y[0]), IntSize(videoMode.width(), videoMode.height())),
            Rect(IntPosition(usableX[0], usableY[0]), IntSize(usableWidth[0], usableHeight[0])),
            videoMode.refreshRate()
        )
    }

    fun getNumMonitors(): Int? = glfwGetMonitors()?.limit()
    fun getPrimaryMonitorInfo(): MonitorInfo? = this.getInfoFromHandle(glfwGetPrimaryMonitor())
    fun listInfo(): List<MonitorInfo?> {
        val monitorsHandles: PointerBuffer = glfwGetMonitors() ?: return emptyList()
        return List(monitorsHandles.remaining()) {
            this.getInfoFromHandle(monitorsHandles.get())
        }
    }

    fun refreshInfo(info: MonitorInfo): MonitorInfo? = this.getInfoFromHandle(info.getHandle())
    fun getInfoAtIndex(i: Int): MonitorInfo? {
        val monitorsHandles: PointerBuffer = glfwGetMonitors() ?: return null
        if (i !in 0 until monitorsHandles.limit()) { return null }
        return this.getInfoFromHandle(monitorsHandles[i])
    }
}
