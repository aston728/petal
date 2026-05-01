package com.aston728.engine.internals.core

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.NULL

import com.aston728.engine.layout.Rect
import com.aston728.engine.layout.BorderSize

import com.aston728.engine.internals.devices.Cursor

import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.IntSize

internal class InternalWindow(shouldHaveDebugContext: Boolean) {
    private var handle: Long = this.create(shouldHaveDebugContext)

    private var savedPosition: IntPosition = if (this.exists()) { this.getPosition() } else { IntPosition() }
    private var savedSize: IntSize = if (this.exists()) { this.getSize() } else { IntSize() }
    private var minimumSize: IntSize? = null
    private var maximumSize: IntSize? = null

    private var isMinimized: Boolean = false
    private var isMaximized: Boolean = false
    private var isFullscreen: Boolean = false

    private var cursor: Cursor = Cursor.ARROW
    private var glfwCursor: Long = glfwCreateStandardCursor(this.cursor.toInt())

    private fun create(shouldHaveDebugContext: Boolean): Long {
        glfwDefaultWindowHints()

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        if (shouldHaveDebugContext) { glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE) }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        return glfwCreateWindow(1, 1, "", NULL, NULL)
    }

    internal fun getHandle(): Long = this.handle
    internal fun exists(): Boolean = this.handle != NULL
    internal fun shouldClose(): Boolean = glfwWindowShouldClose(this.handle)
    internal fun getTitle(): String? = glfwGetWindowTitle(this.handle)
    internal fun getPosition(): IntPosition {
        val x: IntArray = IntArray(1)
        val y: IntArray = IntArray(1)
        glfwGetWindowPos(this.handle, x, y)
        val frameSize: BorderSize = this.getFrameSize()
        return IntPosition(x[0] - frameSize.left, y[0] - frameSize.top)
    }
    internal fun getSavedPosition(): IntPosition = this.savedPosition
    internal fun getSize(): IntSize {
        val width: IntArray = IntArray(1)
        val height: IntArray = IntArray(1)
        glfwGetWindowSize(this.handle, width, height)
        return IntSize(width[0], height[0])
    }
    internal fun getSavedSize(): IntSize = this.savedSize
    internal fun getFrameSize(): BorderSize {
        val left: IntArray = IntArray(1)
        val top: IntArray = IntArray(1)
        val right: IntArray = IntArray(1)
        val bottom: IntArray = IntArray(1)
        glfwGetWindowFrameSize(this.handle, left, top, right, bottom)
        return BorderSize(left[0], top[0], right[0], bottom[0])
    }
    internal fun getRect(): Rect {
        val position: IntPosition = this.getPosition()
        val size: IntSize = this.getSize()
        val frameSize: BorderSize = this.getFrameSize()
        return Rect(
            IntPosition(position.x - frameSize.left, position.y - frameSize.top),
            IntSize(size.width + frameSize.left + frameSize.right, size.height + frameSize.top + frameSize.bottom)
        )
    }
    internal fun getSavedRect(): Rect {
        val frameSize: BorderSize = this.getFrameSize()
        return Rect(
            this.savedPosition,
            IntSize(this.savedSize.width + frameSize.left + frameSize.right, this.savedSize.height + frameSize.top + frameSize.bottom)
        )
    }
    internal fun getMinimumSize(): IntSize? = this.minimumSize
    internal fun getMaximumSize(): IntSize? = this.maximumSize
    internal fun isFocused(): Boolean = glfwGetWindowAttrib(this.handle, GLFW_FOCUSED) != 0
    internal fun isMinimized(): Boolean = this.isMinimized
    internal fun isMaximized(): Boolean = this.isMaximized
    internal fun isFullscreen(): Boolean = this.isFullscreen
    internal fun isNormal(): Boolean = (
        glfwGetWindowAttrib(this.handle, GLFW_ICONIFIED) == 0 &&
        glfwGetWindowAttrib(this.handle, GLFW_MAXIMIZED) == 0 &&
        glfwGetWindowMonitor(this.handle) == NULL
    )
    internal fun getOpacity(): Float = glfwGetWindowOpacity(this.handle)
    internal fun isResizable(): Boolean = glfwGetWindowAttrib(this.handle, GLFW_RESIZABLE) != 0
    internal fun isBordered(): Boolean = glfwGetWindowAttrib(this.handle, GLFW_DECORATED) != 0
    internal fun isAlwaysOnTop(): Boolean = glfwGetWindowAttrib(this.handle, GLFW_FLOATING) != 0
    internal fun getCursor(): Cursor = this.cursor

    internal fun show(): InternalWindow = apply {
        glfwShowWindow(this.handle)
    }
    internal fun hide(): InternalWindow = apply {
        glfwHideWindow(this.handle)
    }
    internal fun setShouldClose(shouldClose: Boolean): InternalWindow = apply {
        glfwSetWindowShouldClose(this.handle, shouldClose)
    }
    internal fun setTitle(title: String): InternalWindow = apply {
        glfwSetWindowTitle(this.handle, title)
    }
    internal fun setPosition(position: IntPosition): InternalWindow = apply {
        this.savedPosition = position
        if (this.isNormal()) {
            val frameSize: BorderSize = this.getFrameSize()
            glfwSetWindowPos(this.handle, this.savedPosition.x + frameSize.left, this.savedPosition.y + frameSize.top)

            val actualPosition: IntPosition = this.getPosition()
            if (actualPosition != this.savedPosition) { this.savedPosition = actualPosition }
        }
    }
    internal fun setSize(size: IntSize): InternalWindow = apply {
        this.savedSize = size
        if (this.isNormal()) {
            glfwSetWindowSize(this.handle, this.savedSize.width, this.savedSize.height)

            val actualSize: IntSize = this.getSize()
            if (actualSize != this.savedSize) { this.savedSize = actualSize }
        }
    }
    internal fun setMinimumSize(size: IntSize?): InternalWindow = apply {
        this.minimumSize = size
        glfwSetWindowSizeLimits(
            this.handle,
            this.minimumSize?.width ?: GLFW_DONT_CARE, this.minimumSize?.height ?: GLFW_DONT_CARE,
            this.maximumSize?.width ?: GLFW_DONT_CARE, this.maximumSize?.height ?: GLFW_DONT_CARE
        )
        if (size != null) { this.savedSize = this.savedSize.flooredAt(size) }
    }
    internal fun setMaximumSize(size: IntSize?): InternalWindow = apply {
        this.maximumSize = size
        glfwSetWindowSizeLimits(
            this.handle,
            this.minimumSize?.width ?: GLFW_DONT_CARE, this.minimumSize?.height ?: GLFW_DONT_CARE,
            this.maximumSize?.width ?: GLFW_DONT_CARE, this.maximumSize?.height ?: GLFW_DONT_CARE
        )
        if (size != null) { this.savedSize = this.savedSize.ceiledAt(size) }
    }
    internal fun focus(): InternalWindow = apply {
        glfwFocusWindow(this.handle)
    }
    internal fun setOpacity(opacity: Float): InternalWindow = apply {
        glfwSetWindowOpacity(this.handle, opacity)
    }
    internal fun setResizable(isResizable: Boolean): InternalWindow = apply {
        glfwSetWindowAttrib(
            this.handle, GLFW_RESIZABLE,
            if (isResizable) { 1 } else { 0 }
        )
    }
    internal fun setBordered(isBordered: Boolean): InternalWindow = apply {
        glfwSetWindowAttrib(
            this.handle, GLFW_DECORATED,
            if (isBordered) { 1 } else { 0 }
        )
    }
    internal fun setAlwaysOnTop(isAlwaysOnTop: Boolean): InternalWindow = apply {
        glfwSetWindowAttrib(
            this.handle, GLFW_FLOATING,
            if (isAlwaysOnTop) { 1 } else { 0 }
        )
    }
    internal fun requestAttention(): InternalWindow = apply {
        glfwRequestWindowAttention(this.handle)
    }
    internal fun setCursor(cursor: Cursor): InternalWindow = apply {
        glfwDestroyCursor(this.glfwCursor)
        this.cursor = cursor
        this.glfwCursor = glfwCreateStandardCursor(this.cursor.toInt())
        glfwSetCursor(this.handle, this.glfwCursor)
    }
    internal fun setOnCloseRequestCallback(callback: (Long) -> Unit): InternalWindow = apply {
        glfwSetWindowCloseCallback(this.handle, callback)
    }
    internal fun setOnMoveCallback(callback: (Long, Int, Int) -> Unit): InternalWindow = apply {
        glfwSetWindowPosCallback(this.handle, callback)
    }
    internal fun setOnResizeCallback(callback: (Long, Int, Int) -> Unit): InternalWindow = apply {
        glfwSetFramebufferSizeCallback(this.handle, callback)
    }
    internal fun setOnMinimizeChangeCallback(callback: (Long, Boolean) -> Unit): InternalWindow = apply {
        glfwSetWindowIconifyCallback(this.handle, callback)
    }
    internal fun setOnMaximizeChangeCallback(callback: (Long, Boolean) -> Unit): InternalWindow = apply {
        glfwSetWindowMaximizeCallback(this.handle, callback)
    }
    internal fun setOnFocusChangeCallback(callback: (Long, Boolean) -> Unit): InternalWindow = apply {
        glfwSetWindowFocusCallback(this.handle, callback)
    }
    internal fun setOnMouseEnterLeaveCallback(callback: (Long, Boolean) -> Unit): InternalWindow = apply {
        glfwSetCursorEnterCallback(this.handle, callback)
    }
    internal fun setOnMouseMoveCallback(callback: (Long, Double, Double) -> Unit): InternalWindow = apply {
        glfwSetCursorPosCallback(this.handle, callback)
    }
    internal fun setOnMouseScrollCallback(callback: (Long, Double, Double) -> Unit): InternalWindow = apply {
        glfwSetScrollCallback(this.handle, callback)
    }
    internal fun setOnMouseButtonCallback(callback: (Long, Int, Int, Int) -> Unit): InternalWindow = apply {
        glfwSetMouseButtonCallback(this.handle, callback)
    }
    internal fun setOnKeyCallback(callback: (Long, Int, Int, Int, Int) -> Unit): InternalWindow = apply {
        glfwSetKeyCallback(this.handle, callback)
    }
    internal fun setOnCharacterCallback(callback: (Long, Int) -> Unit): InternalWindow = apply {
        glfwSetCharCallback(this.handle, callback)
    }

    internal fun setModes(
        isMinimized: Boolean, isMaximized: Boolean, isFullscreen: Boolean,
        monitorInfo: MonitorInfo?
    ): Unit {
        // Reset states and apply new ones

        if (this.isFullscreen) {
            glfwSetWindowMonitor(
                this.handle, NULL,
                0, 0, 1, 1,
                GLFW_DONT_CARE
            )
            this.isFullscreen = false
        }
        if (this.isMaximized) {
            glfwRestoreWindow(this.handle)
            this.isMaximized = false
        }
        if (this.isMinimized) {
            glfwRestoreWindow(this.handle)
            this.isMinimized = false
        }
        this.setSize(this.savedSize).setPosition(this.savedPosition)

        this.isMinimized = isMinimized
        this.isMaximized = isMaximized
        this.isFullscreen = isFullscreen
        if (this.isMaximized) {
            glfwMaximizeWindow(this.handle)
        }
        if (this.isFullscreen && monitorInfo != null) {
            glfwSetWindowMonitor(
                this.handle, monitorInfo.getHandle(),
                0, 0,
                monitorInfo.getRect().width, monitorInfo.getRect().height,
                monitorInfo.getRefreshRate(),
            )
        }
        if (this.isMinimized) {
            glfwIconifyWindow(this.handle)
        }
    }

    internal fun onClose(): Unit {
        glfwDestroyCursor(this.glfwCursor)
        glfwDestroyWindow(this.handle)
        this.handle = NULL
    }
    internal fun onMove(position: IntPosition): Unit {
        if (this.isNormal()) {
            val frameSize: BorderSize = this.getFrameSize()
            this.setPosition(IntPosition(position.x - frameSize.left, position.y - frameSize.top))
        }
    }
    internal fun onResize(size: IntSize): Unit {
        if (this.isNormal()) { this.setSize(size) }
    }
    internal fun onMinimize(): Unit {
        this.isMinimized = true
    }
    internal fun onUnminimize(): Unit {
        this.isMinimized = false
        this.setSize(this.savedSize).setPosition(this.savedPosition)
    }
    internal fun onMaximize(): Unit {
        this.isMaximized = true
    }
    internal fun onUnmaximize(): Unit {
        this.isMaximized = false
        this.setSize(this.savedSize).setPosition(this.savedPosition)
    }
}
