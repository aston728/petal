package com.aston728.engine.core.internals

import com.aston728.engine.core.geometry.BorderSize
import com.aston728.engine.core.geometry.Rect
import com.aston728.engine.core.geometry.IntPosition
import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.internals.devices.Cursor
import com.aston728.engine.core.internals.devices.DeviceAction
import com.aston728.engine.core.internals.devices.Key
import com.aston728.engine.core.internals.devices.MouseButton
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWDropCallback
import org.lwjgl.glfw.GLFWNativeWin32
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.windows.User32

internal class InternalWindow(sharedContextHandle: Long?, shouldHaveDebugContext: Boolean) {
    // TODO: how to content scale
    private var handle: Long = this.create(sharedContextHandle, shouldHaveDebugContext)

    private var savedPosition: IntPosition = if (this.exists()) { this.getPosition() } else { IntPosition() }
    private var savedSize: IntSize = if (this.exists()) { this.getSize() } else { IntSize() }
    private var minimumSize: IntSize? = null
    private var maximumSize: IntSize? = null

    private var isVisible: Boolean = false
    private var isMinimized: Boolean = false
    private var isMaximized: Boolean = false
    private var isFullscreen: Boolean = false

    private var pendingPosition: IntPosition? = null
    private var pendingSize: IntSize? = null
    private var didUnminimize: Boolean = false
    private var didUnmaximize: Boolean = false

    private var cursor: Cursor = Cursor.ARROW
    private var glfwCursor: Long = glfwCreateStandardCursor(this.cursor.toGLFWCursor())

    private fun create(sharedContextHandle: Long?, shouldHaveDebugContext: Boolean): Long {
        glfwDefaultWindowHints()

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        if (shouldHaveDebugContext) {
            glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE)
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        return glfwCreateWindow(1, 1, "", NULL, sharedContextHandle ?: NULL)
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
    internal fun isNormal(): Boolean = !(this.isMinimized || this.isMaximized || this.isFullscreen)
    internal fun getOpacity(): Float = glfwGetWindowOpacity(this.handle)
    internal fun isResizable(): Boolean = glfwGetWindowAttrib(this.handle, GLFW_RESIZABLE) != 0
    internal fun isBordered(): Boolean = glfwGetWindowAttrib(this.handle, GLFW_DECORATED) != 0
    internal fun isAlwaysOnTop(): Boolean = glfwGetWindowAttrib(this.handle, GLFW_FLOATING) != 0
    internal fun getCursor(): Cursor = this.cursor

    private fun isUtilityWin32(): Boolean {
        val win32Handle: Long = GLFWNativeWin32.glfwGetWin32Window(this.handle)
        val exStyle: Long = User32.GetWindowLongPtr(win32Handle, User32.GWL_EXSTYLE)
        return (exStyle and User32.WS_EX_TOOLWINDOW.toLong()) != 0L
    }
    internal fun isUtility(): Boolean {
        val platform: Int = glfwGetPlatform()
        if (platform == GLFW_PLATFORM_WIN32) { return this.isUtilityWin32() }
        return false
    }

    internal fun show(): InternalWindow = apply {
        this.isVisible = true
        glfwShowWindow(this.handle)
    }
    internal fun hide(): InternalWindow = apply {
        this.isVisible = false
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
        this.glfwCursor = glfwCreateStandardCursor(this.cursor.toGLFWCursor())
        glfwSetCursor(this.handle, this.glfwCursor)
    }

    private fun setUtilityWin32(isUtility: Boolean) {
        val win32Handle: Long = GLFWNativeWin32.glfwGetWin32Window(this.handle)
        var exStyle: Long = User32.GetWindowLongPtr(win32Handle, User32.GWL_EXSTYLE)
        exStyle =
            if (isUtility) { exStyle or User32.WS_EX_TOOLWINDOW.toLong() and User32.WS_EX_APPWINDOW.inv().toLong() }
            else { exStyle and User32.WS_EX_TOOLWINDOW.inv().toLong() }
        User32.SetWindowLongPtr(null, win32Handle, User32.GWL_EXSTYLE, exStyle)
    }
    internal fun setUtility(isUtility: Boolean): InternalWindow = apply {
        val platform = glfwGetPlatform()
        if (platform == GLFW_PLATFORM_WIN32) { this.setUtilityWin32(isUtility) }
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
    internal fun setOnMouseButtonCallback(callback: (Long, MouseButton, DeviceAction, Int) -> Unit): InternalWindow = apply {
        glfwSetMouseButtonCallback(this.handle) { window, button, action, mods ->
            callback(window, MouseButton.fromGLFWButton(button), DeviceAction.fromGLFWAction(action), mods)
        }
    }
    internal fun setOnKeyCallback(callback: (Long, Key, Int, DeviceAction, Int) -> Unit): InternalWindow = apply {
        glfwSetKeyCallback(this.handle) { window, key, scanCode, action, mods ->
            callback(window, Key(key), scanCode, DeviceAction.fromGLFWAction(action), mods)
        }
    }
    internal fun setOnCharacterCallback(callback: (Long, Int) -> Unit): InternalWindow = apply {
        glfwSetCharCallback(this.handle, callback)
    }
    internal fun setOnDropCallback(callback: (Long, List<String>) -> Unit): InternalWindow = apply {
        glfwSetDropCallback(this.handle) { window, numFiles, files ->
            val fileNames: List<String> = List(numFiles) { i -> GLFWDropCallback.getName(files, i) }
            callback(window, fileNames)
        }
    }

    internal fun setModes(
        isMinimized: Boolean, isMaximized: Boolean, isFullscreen: Boolean,
        monitorInfo: MonitorInfo?,
    ): Unit {
        // Reset states and apply new ones

        if (this.isFullscreen) {
            glfwSetWindowMonitor(this.handle, NULL, 0, 0, 1, 1, GLFW_DONT_CARE)
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

        if (!this.isVisible) { this.hide() }
    }

    internal fun onClose(): Unit {
        glfwDestroyCursor(this.glfwCursor)
        glfwDestroyWindow(this.handle)
        this.handle = NULL
    }
    internal fun onMove(position: IntPosition): Unit {
        this.pendingPosition = position
    }
    internal fun onResize(size: IntSize): Unit {
        this.pendingSize = size
    }
    internal fun onMinimize(): Unit {
        this.isMinimized = true
    }
    internal fun onUnminimize(): Unit {
        this.isMinimized = false
        this.didUnminimize = true
        this.pendingPosition = null
        this.pendingSize = null
    }
    internal fun onMaximize(): Unit {
        this.isMaximized = true
    }
    internal fun onUnmaximize(): Unit {
        this.isMaximized = false
        this.didUnmaximize = true
        this.pendingPosition = null
        this.pendingSize = null
    }

    internal fun afterEvents(): Unit {
        val position: IntPosition? = this.pendingPosition
        if (position != null && this.isNormal()) {
            val frameSize: BorderSize = this.getFrameSize()
            this.savedPosition = IntPosition(position.x - frameSize.left, position.y - frameSize.top)
        }
        this.pendingPosition = null

        val size: IntSize? = this.pendingSize
        if (size != null && this.isNormal()) { this.savedSize = size }
        this.pendingSize = null

        if (this.didUnminimize) {
            this.setSize(this.savedSize).setPosition(this.savedPosition)
            this.didUnminimize = false
        }
        if (this.didUnmaximize) {
            this.setSize(this.savedSize).setPosition(this.savedPosition)
            this.didUnmaximize = false
        }
    }
}
