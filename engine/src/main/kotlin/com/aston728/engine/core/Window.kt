package com.aston728.engine.core

import kotlin.reflect.KClass

import com.aston728.engine.renderer.GraphicsContext
import com.aston728.engine.renderer.GraphicsApi
import com.aston728.engine.renderer.BlankGraphicsContext

import com.aston728.engine.layout.Rect
import com.aston728.engine.layout.BorderSize
import com.aston728.engine.layout.Coordinate

import com.aston728.engine.utils.Handle
import com.aston728.engine.utils.NamedObject

import com.aston728.engine.internals.core.InternalWindow
import com.aston728.engine.internals.core.MonitorInfo

import com.aston728.engine.internals.renderer.OpenGLContext
import com.aston728.engine.internals.renderer.VulkanContext

import com.aston728.engine.internals.devices.Cursor

import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.IntSize
import com.aston728.engine.types.Handler
import com.aston728.engine.types.ErrorHandler

class Window internal constructor(
    private var context: EngineContext, sharedHandle: Long?, graphicsApi: GraphicsApi,
    debugMessageCallback: ErrorHandler
) : NamedObject<Window>("Unnamed Window") {
    private var initialSize: IntSize = IntSize(500, 500)
    private val internalWindow: InternalWindow = this.createInternalWindow(sharedHandle)
    private var state: WindowState = if (this.internalWindow.exists()) { WindowState.ALIVE } else { WindowState.NONEXISTING }

    private var defaultCursor: Cursor = Cursor.ARROW
    private var topLevelCursor: Cursor? = null

    private var ui: UI? = null

    private val graphicsContext: GraphicsContext = this.createGraphicsContext(graphicsApi, debugMessageCallback)

    private fun createInternalWindow(sharedHandle: Long?): InternalWindow {
        val window: InternalWindow = InternalWindow(sharedHandle, this.context.isDebugOn)
        if (window.exists()) {
            window
                .setTitle("Untitled Window")
                .setSize(this.initialSize)
        }
        return window
    }
    private fun createGraphicsContext(graphicsApi: GraphicsApi, debugMessageCallback: ErrorHandler): GraphicsContext {
        if (this.state != WindowState.ALIVE) { return BlankGraphicsContext() }
        return when (graphicsApi) {
            GraphicsApi.OPEN_GL -> OpenGLContext(this.internalWindow.getHandle(), this.context.isDebugOn, debugMessageCallback)
            GraphicsApi.VULKAN -> VulkanContext()
            GraphicsApi.NONE -> BlankGraphicsContext()
        }

    }
    private inline fun <T> safeGet(attributeName: String, block: () -> T): T? {
        if (this.state == WindowState.NONEXISTING) {
            this.context.logger.error("WINDOW", "$this doesn't exist, cannot get $attributeName")
            return null
        }
        return block()
    }
    private inline fun safeSet(attributeName: String, block: Handler): Window {
        if (this.state != WindowState.ALIVE) {
            this.context.logger.error("WINDOW", "$this isn't alive, cannot set $attributeName")
        } else {
            block()
        }
        return this
    }
    private inline fun safeDo(actionDescription: String, block: Handler): Window {
        if (this.state != WindowState.ALIVE) {
            this.context.logger.error("WINDOW", "$this isn't alive, cannot $actionDescription")
        } else {
            block()
        }
        return this
    }

    internal fun getInternalWindow(): InternalWindow = this.internalWindow
    internal fun getGraphicsContext(): GraphicsContext = this.graphicsContext

    fun getState(): WindowState = this.state
    fun getHandle(): Long? = this.safeGet("handle") { this.internalWindow.getHandle() }
    fun getTitle(): String? = this.safeGet("title") { this.internalWindow.getTitle() }
    fun getPosition(): IntPosition? = this.safeGet("position") { this.internalWindow.getPosition() }
    fun getSavedPosition(): IntPosition? = this.safeGet("saved position") { this.internalWindow.getSavedPosition() }
    fun getInitialSize(): IntSize? = this.safeGet("initial size") { this.initialSize }
    fun getSize(): IntSize? = this.safeGet("size") { this.internalWindow.getSize() }
    fun getSavedSize(): IntSize? = this.safeGet("saved size") { this.internalWindow.getSavedSize() }
    fun getFrameSize(): BorderSize? = this.safeGet("border size") { this.internalWindow.getFrameSize() }
    fun getRect(): Rect? = this.safeGet("rect") { this.internalWindow.getRect() }
    fun getSavedRect(): Rect? = this.safeGet("saved rect") { this.internalWindow.getSavedRect() }
    fun getMinimumSize(): IntSize? = this.safeGet("minimum size") { this.internalWindow.getMinimumSize() }
    fun getMaximumSize(): IntSize? = this.safeGet("maximum size") { this.internalWindow.getMaximumSize() }
    fun isFocused(): Boolean? = this.safeGet("focused flag") { this.internalWindow.isFocused() }
    fun isMinimized(): Boolean? = this.safeGet("minimized flag") { this.internalWindow.isMinimized() }
    fun isMaximized(): Boolean? = this.safeGet("maximized flag") { this.internalWindow.isMaximized() }
    fun isFullscreen(): Boolean? = this.safeGet("fullscreen flag") { this.internalWindow.isFullscreen() }
    fun isNormal(): Boolean? = this.safeGet("normal flag") { this.internalWindow.isNormal() }
    fun getOpacity(): Float? = this.safeGet("opacity") { this.internalWindow.getOpacity() }
    fun isResizable(): Boolean? = this.safeGet("resizable flag") { this.internalWindow.isResizable() }
    fun isBordered(): Boolean? = this.safeGet("bordered flag") { this.internalWindow.isBordered() }
    fun isAlwaysOnTop(): Boolean? = this.safeGet("always on top flag") { this.internalWindow.isAlwaysOnTop() }
    fun isUtility(): Boolean? = this.safeGet("utility flag") { this.internalWindow.isUtility() }
    fun getMonitorInfo(): MonitorInfo? = this.safeGet("monitor info") {
        val monitorsInfo: List<MonitorInfo> = this.context.monitors.listInfo().filterNotNull()
        var windowMonitorInfo: MonitorInfo? = null
        val windowRect: Rect = this.internalWindow.getSavedRect()

        var maxOverlap: Int = -1
        for (monitorInfo in monitorsInfo) {
            val overlap: Int = monitorInfo.getRect().getOverlapArea(windowRect)
            if (overlap > maxOverlap) {
                windowMonitorInfo = monitorInfo
                maxOverlap = overlap
            }
        }

        return windowMonitorInfo ?: this.context.monitors.getPrimaryMonitorInfo()
    }
    fun getCursor(): Cursor? = this.safeGet("cursor") { this.internalWindow.getCursor() }
    fun getDefaultCursor(): Cursor? = this.safeGet("default cursor") { this.defaultCursor }
    fun getTopLevelCursor(): Cursor? = this.safeGet("top level cursor") { this.topLevelCursor }
    fun getUI(): UI? = this.safeGet("UI") { this.ui }

    internal fun show(): Window = apply {
        this.internalWindow.show()
    }
    internal fun hide(): Window = apply {
        this.internalWindow.hide()
    }
    internal fun focus(): Window = apply {
        this.internalWindow.focus()
    }
    internal fun setShouldClose(shouldClose: Boolean): Window = apply {
        this.internalWindow.setShouldClose(shouldClose)
    }
    internal fun startClosing(): Window = apply {
        this.internalWindow.hide()
        this.state = WindowState.CLOSING
    }
    internal fun setCursor(cursor: Cursor?): Window = apply {
        val cursor: Cursor = this.topLevelCursor ?: cursor ?: this.defaultCursor
        if (cursor != this.internalWindow.getCursor()) { this.internalWindow.setCursor(cursor) }
    }

    fun setTitle(title: String): Window = this.safeSet("title") {
        this.internalWindow.setTitle(title)
    }
    fun setPosition(position: IntPosition, monitorI: Int? = null): Window = this.safeSet("position") {
        val monitorInfo: MonitorInfo? =
            if (monitorI == null) { this.getMonitorInfo() }
            else { this.context.monitors.getInfoAtIndex(monitorI) }
        if (monitorInfo != null) {
            val monitorPosition: IntPosition = monitorInfo.getRect().getTopLeft()
            this.internalWindow.setPosition(monitorPosition + position)
        }
    }
    fun setPosition(coordinate: Coordinate, monitorI: Int? = null): Window = this.safeSet("position") {
        val monitorInfo: MonitorInfo? =
            if (monitorI == null) { this.getMonitorInfo() }
            else { this.context.monitors.getInfoAtIndex(monitorI) }
        if (monitorInfo != null) {
            val monitorRect: Rect = monitorInfo.getUsableRect()
            val windowRect: Rect = this.internalWindow.getSavedRect()
            windowRect.setByCoordinate(coordinate, monitorRect.getByCoordinate(coordinate))
            this.internalWindow.setPosition(windowRect.getTopLeft())
        }
    }
    fun setInitialSize(size: IntSize): Window = this.safeSet("initial size") {
        this.initialSize = size
    }
    fun setSize(size: IntSize): Window = this.safeSet("size") {
        var size: IntSize = size
        val minimumSize: IntSize? = this.internalWindow.getMinimumSize()
        val maximumSize: IntSize? = this.internalWindow.getMaximumSize()
        if (minimumSize != null) {
            if (size.width < minimumSize.width) {
                this.context.logger.warn("WINDOW", "$this has a width smaller than its minimum width: ${size.width}, ${minimumSize.width}")
            }
            if (size.height < minimumSize.height) {
                this.context.logger.warn("WINDOW", "$this has a height smaller than its minimum height: ${size.height}, ${minimumSize.height}")
            }
            size = size.flooredAt(minimumSize)
        }
        if (maximumSize != null) {
            if (size.width > maximumSize.width) {
                this.context.logger.warn("WINDOW", "$this has a width bigger than its maximum width: ${size.width}, ${maximumSize.width}")
            }
            if (size.height > maximumSize.height) {
                this.context.logger.warn("WINDOW", "$this has a height bigger than its maximum height: ${size.height}, ${maximumSize.height}")
            }
            size = size.ceiledAt(maximumSize)
        }
        this.internalWindow.setSize(size)
    }
    fun setMinimumSize(size: IntSize?): Window = this.safeSet("minimum size") {
        var size: IntSize? = size
        val maximumSize: IntSize? = this.internalWindow.getMaximumSize()
        if (size != null && maximumSize != null) {
            if (size.width > maximumSize.width) {
                this.context.logger.warn("WINDOW", "$this has a minimum width bigger than its maximum width: ${size.width} ${maximumSize.width}")
            }
            if (size.height > maximumSize.height) {
                this.context.logger.warn("WINDOW", "$this has a minimum height bigger than its maximum height: ${size.height} ${maximumSize.height}")
            }
            size = size.ceiledAt(maximumSize)
        }
        this.internalWindow.setMinimumSize(size)
    }
    fun setMaximumSize(size: IntSize?): Window = this.safeSet("maximum size") {
        var size: IntSize? = size
        val minimumSize: IntSize? = this.internalWindow.getMinimumSize()
        if (size != null && minimumSize != null) {
            if (size.width < minimumSize.width) {
                this.context.logger.warn("WINDOW", "$this has a maximum width smaller than its minimum width: ${size.width} ${minimumSize.width}")
            }
            if (size.height < minimumSize.height) {
                this.context.logger.warn("WINDOW", "$this has a maximum height smaller than its minimum height: ${size.height} ${minimumSize.height}")
            }
            size = size.flooredAt(minimumSize)
        }
        this.internalWindow.setMaximumSize(size)
    }
    fun setMinimized(isMinimized: Boolean): Window = this.safeSet("minimized flag") {
        if (this.internalWindow.isMinimized() != isMinimized) {
            this.internalWindow.setModes(
                isMinimized, this.internalWindow.isMaximized(), this.internalWindow.isFullscreen(),
                monitorInfo = null
            )
        }
    }
    fun setMaximized(isMaximized: Boolean): Window = this.safeSet("maximized flag") {
        if (this.internalWindow.isMaximized() != isMaximized) {
            this.internalWindow.setModes(
                this.internalWindow.isMinimized(), isMaximized, this.internalWindow.isFullscreen(),
                monitorInfo = null
            )
        }
    }
    fun setFullscreen(isFullscreen: Boolean, monitorI: Int? = null): Window = this.safeSet("fullscreen flag") {
        val windowMonitorInfo: MonitorInfo? = this.getMonitorInfo()
        val monitorInfo: MonitorInfo? =
            if (monitorI == null) { windowMonitorInfo }
            else { this.context.monitors.getInfoAtIndex(monitorI) }
        if (this.internalWindow.isFullscreen() != isFullscreen || monitorInfo != windowMonitorInfo) {
            this.internalWindow.setModes(
                this.internalWindow.isMinimized(), this.internalWindow.isMaximized(), isFullscreen,
                monitorInfo
            )
        }
    }
    fun toggleFullscreen(monitorI: Int? = null): Window = this.setFullscreen(!this.internalWindow.isFullscreen(), monitorI)
    fun setOpacity(opacity: Float): Window = this.safeSet("opacity") {
        var opacity: Float = opacity
        if (opacity !in 0.0..1.0) {
            this.context.logger.warn("WINDOW", "$this has an invalid opacity: $opacity")
            opacity = if (opacity < 0) { 0.0f } else { 1.0f }
        }
        this.internalWindow.setOpacity(opacity)
    }
    fun setResizable(isResizable: Boolean): Window = this.safeSet("resizable flag") {
        this.internalWindow.setResizable(isResizable)
    }
    fun setBordered(isBordered: Boolean): Window = this.safeSet("bordered flag") {
        this.internalWindow.setBordered(isBordered)
    }
    fun setAlwaysOnTop(isAlwaysOnTop: Boolean): Window = this.safeSet("always on top flag") {
        this.internalWindow.setAlwaysOnTop(isAlwaysOnTop)
    }
    fun requestAttention(): Window = this.safeSet("request attention flag") {
        this.internalWindow.requestAttention()
    }
    fun setUtility(isUtility: Boolean): Window = this.safeSet("utility flag") {
        this.internalWindow.setUtility(isUtility)
    }
    fun setDefaultCursor(cursor: Cursor): Window = this.safeSet("default cursor") {
        this.defaultCursor = cursor
    }
    fun setTopLevelCursor(cursor: Cursor?): Window = this.safeSet("top level cursor") {
        this.topLevelCursor = cursor
    }
    fun <T : WindowEvent> addEventHandler(event: KClass<T>, handler: (T) -> Unit, handle: Handle? = null): Window = this.safeDo("add event handler") {
        val wrapper: (T) -> Unit = { event -> if (event.window == this) { handler(event) } }
        this.context.eventSubscriber.subscribe(event, wrapper, handle)
    }

    internal fun attachContext(context: EngineContext): Window = this.safeSet("context") {
        if (this.context != context) {
            this.context = context
            this.ui?.attachContext(this.context)
        }
    }
    fun setUI(ui: UI?): Window = this.safeSet("UI") {
        this.ui = ui?.attachContext(this.context)
        this.ui?.onWindowAttach()
    }
    fun addToEngine(engine: Engine): Window = this.safeDo("add to engine") {
        engine.addWindow(this)
    }
    fun setStructure(ui: UI): Window = this.safeSet("structure") { this.setUI(ui) }

    fun reset(): Window = this.safeDo("reset") {
        this.internalWindow.setModes(
            isMinimized = false, isMaximized = false, isFullscreen = false,
            monitorInfo = null
        )
        this.setSize(this.initialSize).setPosition(Coordinate.CENTER)
    }

    internal fun onCloseRequest(): Unit {}
    internal fun onClose(): Unit {
        this.internalWindow.onClose()
        this.state = WindowState.NONEXISTING
    }
    internal fun onMove(position: IntPosition): Unit {
        this.internalWindow.onMove(position)
    }
    internal fun onResize(size: IntSize): Unit {
        this.internalWindow.onResize(size)
        this.graphicsContext.onResize(size)
        this.ui?.onResize()
    }
    internal fun onMinimize(): Unit {
        this.internalWindow.onMinimize()
    }
    internal fun onUnminimize(): Unit {
        this.internalWindow.onUnminimize()
    }
    internal fun onMaximize(): Unit {
        this.internalWindow.onMaximize()
    }
    internal fun onUnmaximize(): Unit {
        this.internalWindow.onUnmaximize()
    }
    internal fun onFocus(): Unit {
        this.ui?.onFocus()
    }
    internal fun onUnfocus(): Unit {
        this.ui?.onUnfocus()
    }

    internal fun handleUIDirtyFlags(): Window = apply {
        val rect: Rect = Rect(IntPosition(0, 0), this.internalWindow.getSize())
        this.ui
            ?.handleDirtyAnchors(rect)
            ?.handleDirtyRelationship()
            ?.handleDirtyLayouts(IntSize(rect.width, rect.height))
            ?.handleDirtyShaders(this.graphicsContext)
    }
}
