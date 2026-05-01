package com.aston728.engine.internals.devices

import java.util.EnumSet

import org.lwjgl.glfw.GLFW.glfwGetCursorPos

import com.aston728.engine.types.DecimalOffset
import com.aston728.engine.types.DecimalPosition

internal class MouseController : Mouse {
    private var position: DecimalPosition = DecimalPosition()
    private var scrollOffset: DecimalOffset = DecimalOffset()

    private val pressed: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)
    private val justPressed: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)
    private val justReleased: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)

    override fun getPosition(): DecimalPosition = this.position
    override fun getScrollOffset(): DecimalOffset = this.scrollOffset
    override fun isPressed(button: MouseButton): Boolean = button in this.pressed
    override fun isJustPressed(button: MouseButton): Boolean = button in this.justPressed
    override fun isJustReleased(button: MouseButton): Boolean = button in this.justReleased

    internal fun cleanup(): Unit {
        this.scrollOffset = DecimalOffset()
        this.justPressed.clear()
        this.justReleased.clear()
    }

    internal fun onEnter(windowHandle: Long): Unit {
        val x: DoubleArray = DoubleArray(1)
        val y: DoubleArray = DoubleArray(1)
        glfwGetCursorPos(windowHandle, x, y)
        this.position = DecimalPosition(x[0], y[0])
    }
    internal fun onLeave(windowHandle: Long): Unit {
        val x: DoubleArray = DoubleArray(1)
        val y: DoubleArray = DoubleArray(1)
        glfwGetCursorPos(windowHandle, x, y)
        this.position = DecimalPosition(x[0], y[0])
    }
    internal fun onMove(position: DecimalPosition): Unit {
        this.position = position
    }
    internal fun onScroll(offset: DecimalOffset): Unit {
        this.scrollOffset = offset
    }
    internal fun onPress(button: MouseButton): Unit {
        this.pressed.add(button)
        this.justPressed.add(button)
    }
    internal fun onRelease(button: MouseButton): Unit {
        this.pressed.remove(button)
        this.justReleased.add(button)
    }
}
