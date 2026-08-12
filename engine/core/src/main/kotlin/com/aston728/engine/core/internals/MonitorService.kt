package com.aston728.engine.core.internals

import com.aston728.engine.core.geometry.IntPosition
import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.geometry.Rect
import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWVidMode

public class MonitorService internal constructor() {
    internal fun getInfoFromHandle(handle: Long): MonitorInfo? {
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

    public fun getNumMonitors(): Int? = glfwGetMonitors()?.limit()
    public fun getPrimaryMonitorInfo(): MonitorInfo? = this.getInfoFromHandle(glfwGetPrimaryMonitor())
    public fun listInfo(): List<MonitorInfo?> {
        val monitorsHandles: PointerBuffer = glfwGetMonitors() ?: return emptyList()
        return List(monitorsHandles.remaining()) { this.getInfoFromHandle(monitorsHandles.get()) }
    }

    public fun refreshInfo(info: MonitorInfo): MonitorInfo? = this.getInfoFromHandle(info.getHandle())
    public fun getInfoAtIndex(i: Int): MonitorInfo? {
        val monitorsHandles: PointerBuffer = glfwGetMonitors() ?: return null
        if (i !in 0 until monitorsHandles.limit()) { return null }
        return this.getInfoFromHandle(monitorsHandles[i])
    }
}
