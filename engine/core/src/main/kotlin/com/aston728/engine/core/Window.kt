package com.aston728.engine.core

import com.aston728.engine.core.geometry.*
import com.aston728.engine.core.internals.InternalWindow
import com.aston728.engine.core.internals.MonitorInfo
import com.aston728.engine.core.internals.devices.Cursor
import com.aston728.engine.core.utils.Handle
import com.aston728.engine.core.utils.NamedObject
import kotlin.reflect.KClass

public class Window internal constructor(private var context: EngineContext, sharedContext: Window?) : NamedObject<Window>("Unnamed Window") {
    private var initialSize: IntSize = IntSize(500, 500)
    private val internalWindow: InternalWindow = this.createInternalWindow(sharedContext)
    private var state: WindowState = if (this.internalWindow.exists()) { WindowState.ALIVE } else { WindowState.NONEXISTING }

    private var defaultCursor: Cursor = Cursor.ARROW
    private var topLevelCursor: Cursor? = null

    private var ui: UI? = null

    private fun createInternalWindow(sharedContext: Window?): InternalWindow {
        val window: InternalWindow = InternalWindow(sharedContext?.getInternalWindow()?.getHandle(), this.context.isDebugOn)
        if (window.exists()) {
            window
                .setTitle("Untitled Window")
                .setSize(this.initialSize)
        }
        return window
    }
    private inline fun <T> safeGet(attributeName: String, block: () -> T): T? {
        if (this.state == WindowState.NONEXISTING) {
            this.context.logger.error("WINDOW", "$this doesn't exist, cannot get $attributeName")
            return null
        }
        return block()
    }
    private inline fun safeSet(attributeName: String, block: () -> Unit): Window {
        if (this.state != WindowState.ALIVE) {
            this.context.logger.error("WINDOW", "$this isn't alive, cannot set $attributeName")
        } else {
            block()
        }
        return this
    }
    private inline fun safeDo(actionDescription: String, block: () -> Unit): Window {
        if (this.state != WindowState.ALIVE) {
            this.context.logger.error("WINDOW", "$this isn't alive, cannot $actionDescription")
        } else {
            block()
        }
        return this
    }

    internal fun getInternalWindow(): InternalWindow = this.internalWindow

    public fun getState(): WindowState = this.state
    public fun getHandle(): Long? = this.safeGet("handle") { this.internalWindow.getHandle() }
    public fun getTitle(): String? = this.safeGet("title") { this.internalWindow.getTitle() }
    public fun getPosition(): IntPosition? = this.safeGet("position") { this.internalWindow.getPosition() }
    public fun getSavedPosition(): IntPosition? = this.safeGet("saved position") { this.internalWindow.getSavedPosition() }
    public fun getInitialSize(): IntSize? = this.safeGet("initial size") { this.initialSize }
    public fun getSize(): IntSize? = this.safeGet("size") { this.internalWindow.getSize() }
    public fun getSavedSize(): IntSize? = this.safeGet("saved size") { this.internalWindow.getSavedSize() }
    public fun getFrameSize(): BorderSize? = this.safeGet("border size") { this.internalWindow.getFrameSize() }
    public fun getRect(): Rect? = this.safeGet("rect") { this.internalWindow.getRect() }
    public fun getSavedRect(): Rect? = this.safeGet("saved rect") { this.internalWindow.getSavedRect() }
    public fun getMinimumSize(): IntSize? = this.safeGet("minimum size") { this.internalWindow.getMinimumSize() }
    public fun getMaximumSize(): IntSize? = this.safeGet("maximum size") { this.internalWindow.getMaximumSize() }
    public fun isFocused(): Boolean? = this.safeGet("focused flag") { this.internalWindow.isFocused() }
    public fun isMinimized(): Boolean? = this.safeGet("minimized flag") { this.internalWindow.isMinimized() }
    public fun isMaximized(): Boolean? = this.safeGet("maximized flag") { this.internalWindow.isMaximized() }
    public fun isFullscreen(): Boolean? = this.safeGet("fullscreen flag") { this.internalWindow.isFullscreen() }
    public fun isNormal(): Boolean? = this.safeGet("normal flag") { this.internalWindow.isNormal() }
    public fun getOpacity(): Float? = this.safeGet("opacity") { this.internalWindow.getOpacity() }
    public fun isResizable(): Boolean? = this.safeGet("resizable flag") { this.internalWindow.isResizable() }
    public fun isBordered(): Boolean? = this.safeGet("bordered flag") { this.internalWindow.isBordered() }
    public fun isAlwaysOnTop(): Boolean? = this.safeGet("always on top flag") { this.internalWindow.isAlwaysOnTop() }
    public fun isUtility(): Boolean? = this.safeGet("utility flag") { this.internalWindow.isUtility() }
    public fun getMonitorInfo(): MonitorInfo? = this.safeGet("monitor info") {
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
    public fun getCursor(): Cursor? = this.safeGet("cursor") { this.internalWindow.getCursor() }
    public fun getDefaultCursor(): Cursor? = this.safeGet("default cursor") { this.defaultCursor }
    public fun getTopLevelCursor(): Cursor? = this.safeGet("top level cursor") { this.topLevelCursor }
    public fun getUI(): UI? = this.safeGet("UI") { this.ui }

    internal fun show(): Window = apply {
        this.internalWindow.show()
    }
    internal fun hide(): Window = apply {
        this.internalWindow.hide()
    }
    internal fun focus(): Window = apply {
        this.internalWindow.focus()
    }
    internal fun startClosing(): Window = apply {
        this.internalWindow.hide()
        this.state = WindowState.CLOSING
    }
    internal fun setCursor(cursor: Cursor?): Window = apply {
        val cursor: Cursor = this.topLevelCursor ?: cursor ?: this.defaultCursor
        if (cursor != this.internalWindow.getCursor()) { this.internalWindow.setCursor(cursor) }
    }

    public fun setTitle(title: String): Window = this.safeSet("title") {
        this.internalWindow.setTitle(title)
    }
    public fun setPosition(position: IntPosition, monitorI: Int? = null): Window = this.safeSet("position") {
        val monitorInfo: MonitorInfo? =
            if (monitorI == null) { this.getMonitorInfo() }
            else { this.context.monitors.getInfoAtIndex(monitorI) }
        if (monitorInfo != null) {
            val monitorPosition: IntPosition = monitorInfo.getRect().getTopLeft()
            this.internalWindow.setPosition(monitorPosition + position)
        }
    }
    public fun setPosition(coordinate: Coordinate, monitorI: Int? = null): Window = this.safeSet("position") {
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
    public fun setInitialSize(size: IntSize): Window = this.safeSet("initial size") {
        this.initialSize = size
    }
    public fun setSize(size: IntSize): Window = this.safeSet("size") {
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
    public fun setMinimumSize(size: IntSize?): Window = this.safeSet("minimum size") {
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
    public fun setMaximumSize(size: IntSize?): Window = this.safeSet("maximum size") {
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
    public fun setMinimized(isMinimized: Boolean): Window = this.safeSet("minimized flag") {
        if (this.internalWindow.isMinimized() != isMinimized) {
            this.internalWindow.setModes(
                isMinimized, this.internalWindow.isMaximized(), this.internalWindow.isFullscreen(),
                monitorInfo = null
            )
        }
    }
    public fun setMaximized(isMaximized: Boolean): Window = this.safeSet("maximized flag") {
        if (this.internalWindow.isMaximized() != isMaximized) {
            this.internalWindow.setModes(
                this.internalWindow.isMinimized(), isMaximized, this.internalWindow.isFullscreen(),
                monitorInfo = null
            )
        }
    }
    public fun setFullscreen(isFullscreen: Boolean, monitorI: Int? = null): Window = this.safeSet("fullscreen flag") {
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
    public fun toggleFullscreen(monitorI: Int? = null): Window = this.setFullscreen(!this.internalWindow.isFullscreen(), monitorI)
    public fun setOpacity(opacity: Float): Window = this.safeSet("opacity") {
        var opacity: Float = opacity
        if (opacity !in 0.0..1.0) {
            this.context.logger.warn("WINDOW", "$this has an invalid opacity: $opacity")
            opacity = if (opacity < 0) { 0.0f } else { 1.0f }
        }
        this.internalWindow.setOpacity(opacity)
    }
    public fun setResizable(isResizable: Boolean): Window = this.safeSet("resizable flag") {
        this.internalWindow.setResizable(isResizable)
    }
    public fun setBordered(isBordered: Boolean): Window = this.safeSet("bordered flag") {
        this.internalWindow.setBordered(isBordered)
    }
    public fun setAlwaysOnTop(isAlwaysOnTop: Boolean): Window = this.safeSet("always on top flag") {
        this.internalWindow.setAlwaysOnTop(isAlwaysOnTop)
    }
    public fun requestAttention(): Window = this.safeSet("request attention flag") {
        this.internalWindow.requestAttention()
    }
    public fun setUtility(isUtility: Boolean): Window = this.safeSet("utility flag") {
        this.internalWindow.setUtility(isUtility)
    }
    public fun setDefaultCursor(cursor: Cursor): Window = this.safeSet("default cursor") {
        this.defaultCursor = cursor
    }
    public fun setTopLevelCursor(cursor: Cursor?): Window = this.safeSet("top level cursor") {
        this.topLevelCursor = cursor
    }
    public fun <T : WindowEvent> addEventHandler(event: KClass<T>, handler: (T) -> Unit, handle: Handle? = null): Window = this.safeDo("add event handler") {
        val wrapper: (T) -> Unit = { event -> if (event.window == this) { handler(event) } }
        this.context.eventSubscriber.subscribe(event, wrapper, handle)
    }

    internal fun attachContext(context: EngineContext): Window = this.safeSet("context") {
        if (this.context != context) {
            this.context = context
            this.ui?.attachContext(this.context)
        }
    }
    public fun setUI(ui: UI?): Window = this.safeSet("UI") {
        this.ui = ui?.attachContext(this.context)
        this.ui?.onWindowAttach()
    }
    public fun addToEngine(engine: Engine): Window = this.safeDo("add to engine") {
        engine.addWindow(this)
    }
    public fun setStructure(ui: UI): Window = this.safeSet("structure") {
        this.setUI(ui)
    }

    public fun reset(): Window = this.safeDo("reset") {
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
    internal fun onMove(position: IntPosition): Unit = this.internalWindow.onMove(position)
    internal fun onResize(size: IntSize): Unit {
        this.internalWindow.onResize(size)
        this.ui?.onResize()
    }
    internal fun onMinimize(): Unit = this.internalWindow.onMinimize()
    internal fun onUnminimize(): Unit = this.internalWindow.onUnminimize()
    internal fun onMaximize(): Unit = this.internalWindow.onMaximize()
    internal fun onUnmaximize(): Unit = this.internalWindow.onUnmaximize()
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
            ?.handleDirtyShaders(this.internalWindow.getHandle())
    }
}
