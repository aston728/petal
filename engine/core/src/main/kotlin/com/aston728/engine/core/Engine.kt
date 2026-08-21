package com.aston728.engine.core

import com.aston728.engine.core.internals.*
import com.aston728.engine.core.internals.devices.Key
import com.aston728.engine.core.internals.devices.KeyboardController
import com.aston728.engine.core.internals.devices.MouseController
import com.aston728.engine.core.rendererBase.GraphicsApi
import com.aston728.engine.core.rendererBase.RenderManager
import com.aston728.engine.core.rendererBase.ShaderManager
import java.util.*

private fun centerString(string: String, fillChar: Char, size: Int): String {
    if (string.length >= size) { return string }

    val fullPadding: Int = size - string.length
    val leftPadding: Int = fullPadding / 2
    val rightPadding: Int = fullPadding - leftPadding
    return fillChar.toString().repeat(leftPadding) + string + fillChar.toString().repeat(rightPadding)
}

public class Engine private constructor(config: EngineConfig) {
    public companion object {
        private var instance: Engine? = null
        public fun create(config: EngineConfig): Engine {
            if (this.instance != null) {
                this.instance!!.logger.error("ENGINE", "Cannot create more than one engine instance")
            } else {
                this.instance = Engine(config)
            }
            return this.instance!!
        }
    }

    private val graphicsApi: GraphicsApi = config.graphicsApi

    private val allWindows: MutableList<Window> = mutableListOf()
    private val activeWindows: MutableSet<Window> = mutableSetOf()
    private var focusedWindow: Window? = null

    private var stopKey: Key? = Key.ESCAPE
    private var resetKey: Key? = Key.F1
    private var fullscreenKey: Key? = Key.F11

    private var fps: Double = 0.0
    private var fpsCap: Int? = null
    private var didStart: Boolean = false

    private val logger: FrozenLogger = this.createLogger(config.engineMode)
    private val runtime: InternalRuntime = InternalRuntime()
    private val renderManager: RenderManager = this.createRenderManager(config.graphicsApi, config.engineMode == EngineMode.DEBUG)

    private val mouse: MouseController = MouseController()
    private val keyboard: KeyboardController = KeyboardController()
    private val eventManager: EventManager = EventManager()
    private val shaderManager: ShaderManager = ShaderManager(
        this.renderManager::buildShader,
        errorCallback = { message -> this.logger.error("SHADER", message) }
    )
    private val context: EngineContext = this.createContext(config.engineMode)

    init {
        this.context.eventSubscriber.subscribe(KeyPressedEvent::class, { event -> when (event.key) {
            this.stopKey -> this.stop()
            this.resetKey -> this.focusedWindow?.reset()
            this.fullscreenKey -> this.focusedWindow?.toggleFullscreen()
        }})
    }

    private fun createLogger(engineMode: EngineMode): Logger {
        val logger: Logger = Logger()
            .setFormat { severity: String, title: String, message: String ->
                val topDivider: String = centerString(severity, '-', 100)
                val bottomDivider: String = "-".repeat(100)
                "$topDivider\n$title\n$message\n$bottomDivider"
            }
            .setHandler { message -> System.err.println(message) }

        when (engineMode) {
            EngineMode.RELEASE -> logger.setShowInfo(false).setShowWarnings(false).setShowErrors(false)
            EngineMode.DEVELOPMENT, EngineMode.DEBUG -> logger.setShowInfo(true).setShowWarnings(true).setShowErrors(true)
        }

        return logger
    }
    private fun createRenderManager(graphicsApi: GraphicsApi, isDebugOn: Boolean): RenderManager {
        val renderManager: RenderManager = ServiceLoader.load(RenderManager::class.java)
            .firstOrNull { it.api == graphicsApi }
            ?: this.logger.fatalError("ENGINE", "Cannot find implementation for graphics API: $graphicsApi")
        return renderManager.setLogger(this.logger).setIsDebugOn(isDebugOn)
    }
    private fun createContext(engineMode: EngineMode): EngineContext {
        val clipboard: ClipboardService = ClipboardService(
            windowProvider = {
                val window: Window? = this.focusedWindow ?: this.activeWindows.firstOrNull()
                window?.getInternalWindow()?.getHandle()
            },
            errorCallback = { message -> this.logger.error("CLIPBOARD", message) }
        )

        return EngineContext(
            this.mouse, this.keyboard,
            MonitorService(), TimeService(), clipboard,
            this.eventManager, AssetManager(this.logger),
            this.shaderManager,
            this.logger, isDebugOn = (engineMode == EngineMode.DEBUG),
        )
    }

    public fun init(): Unit {
        val didSucceed: Boolean = this.runtime.start(errorCallback = { code, description ->
            this.logger.error("RUNTIME", "Internal error ($code): $description")
        })
        if (!didSucceed) { kotlin.system.exitProcess(1) }

        this.runtime
            .setMonitorEventCallback(this.context.monitors) { info, isConnected -> this.eventManager.addEvent(MonitorEvent(info, isConnected))}
    }
    public fun shutdown(): Unit {
        this.runtime.stop()
    }

    public fun getFocusedWindow(): Window? = this.focusedWindow
    public fun getStopKey(): Key? = this.stopKey
    public fun getResetKey(): Key? = this.resetKey
    public fun getFullscreenKey(): Key? = this.fullscreenKey
    public fun getFps(): Double = this.fps
    public fun getFpsCap(): Int? = this.fpsCap
    public fun getContext(): EngineContext = this.context

    public fun setFocusedWindow(window: Window): Engine = apply {
        if (window.getState() != WindowState.ALIVE) {
            this.logger.error("ENGINE", "Cannot focus a non-alive window: $window")
        } else {
            if (window !in this.activeWindows) {
                this.logger.warn("ENGINE", "Focused an absent window: $window")
                this.addWindow(window)
            }
            window.focus()
        }
    }
    public fun setStopKey(key: Key?): Engine = apply {
        this.stopKey = key
    }
    public fun setResetKey(key: Key?): Engine = apply {
        this.resetKey = key
    }
    public fun setFullscreenKey(key: Key?): Engine = apply {
        this.fullscreenKey = key
    }
    public fun setFpsCap(cap: Int?): Engine = apply {
        var cap: Int? = cap
        if (cap != null && cap < 1) {
            this.logger.warn("ENGINE", "Invalid FPS cap: $cap")
            cap = 30
        }
        this.fpsCap = cap
    }

    public fun createWindow(): Window {
        val window: Window = Window(this.context, this.allWindows.firstOrNull())
        if (window.getState() != WindowState.ALIVE) {
            this.logger.error("ENGINE", "Failed to create window")
        } else {
            this.allWindows.add(window)
            this.renderManager.addWindow(window)
        }
        return window
    }
    private fun initWindow(window: Window): Unit {
        val internalWindow: InternalWindow = window.getInternalWindow()
        // not triggered automatically
        this.eventManager
            .addEvent(WindowMoveEvent(window, internalWindow.getPosition()))
            .addEvent(WindowResizeEvent(window, internalWindow.getSize()))
        if (internalWindow.isMinimized()) { this.eventManager.addEvent(WindowMinimizeEvent(window)) }
        else { this.eventManager.addEvent(WindowUnminimizeEvent(window)) }
        if (internalWindow.isMaximized()) { this.eventManager.addEvent(WindowMaximizeEvent(window)) }
        else { this.eventManager.addEvent(WindowUnmaximizeEvent(window)) }

        window.handleUIDirtyFlags().show().focus()
    }
    public fun addWindow(window: Window): Engine = apply {
        if (window.getState() != WindowState.ALIVE) {
            this.logger.error("ENGINE", "Cannot add a non-alive window: $window")
        } else {
            val didAdd: Boolean = this.activeWindows.add(window)
            if (!didAdd) {
                this.logger.warn("ENGINE", "Added an already present window: $window")
            }
            window.attachContext(this.context)
            this.eventManager.registerWindow(window)

            if (this.didStart) { this.initWindow(window) }
        }
    }
    public fun removeWindow(window: Window): Engine = apply {
        if (window.getState() != WindowState.ALIVE) {
            this.logger.error("ENGINE", "Cannot remove a non-alive window: $window")
        } else {
            if (this.focusedWindow == window) { this.focusedWindow = null }

            window.hide()
            this.eventManager.unregisterWindow(window)
            val didRemove: Boolean = this.activeWindows.remove(window)
            if (!didRemove) {
                this.logger.warn("ENGINE", "Removed an already absent window: $window")
            }
        }
    }
    public fun destroyWindow(window: Window): Engine = apply {
        if (window.getState() != WindowState.ALIVE) {
            this.logger.error("ENGINE", "Cannot destroy a non-alive window: $window")
        } else {
            if (this.focusedWindow == window) { this.focusedWindow = null }

            window.startClosing()
            this.eventManager.addEvent(WindowCloseEvent(window))
            this.activeWindows.remove(window)
            this.allWindows.remove(window)
            this.renderManager.removeWindow(window)
        }
    }
    public fun stop(): Engine = apply {
        this.activeWindows.toList().forEach { this.destroyWindow(it) }
    }
    public fun setStructure(vararg windows: Window): Engine = apply {
        this.activeWindows.toList().forEach { this.removeWindow(it) }
        windows.toList().forEach { this.addWindow(it) }
    }

    private fun start(): Unit {
        this.didStart = true
        this.activeWindows.forEach { this.initWindow(it) }
    }
    private fun handleEvent(event: Event): Unit {
        when (event) {
            is MonitorEvent -> {}

            is WindowCloseRequestEvent -> { event.window.onCloseRequest() }
            is WindowCloseEvent -> { event.window.onClose() }
            is WindowMoveEvent -> event.window.onMove(event.position)
            is WindowResizeEvent -> {
                event.window.onResize(event.size)
                this.renderManager.resize(event.window, event.size)
            }
            is WindowMinimizeEvent -> event.window.onMinimize()
            is WindowUnminimizeEvent -> event.window.onUnminimize()
            is WindowMaximizeEvent -> event.window.onMaximize()
            is WindowUnmaximizeEvent -> event.window.onUnmaximize()
            is WindowFocusEvent -> {
                this.focusedWindow = event.window
                event.window.onFocus()
            }
            is WindowUnfocusEvent -> {
                this.focusedWindow = null
                event.window.onUnfocus()
            }

            is MouseEnterEvent -> if (event.window == this.focusedWindow) { this.mouse.onEnter(event.window.getInternalWindow().getHandle()) }
            is MouseLeaveEvent -> if (event.window == this.focusedWindow) { this.mouse.onLeave(event.window.getInternalWindow().getHandle()) }
            is MouseMoveEvent -> if (event.window == this.focusedWindow) { this.mouse.onMove(event.position) }
            is MouseScrollEvent -> if (event.window == this.focusedWindow) { this.mouse.onScroll(event.offset) }
            is MouseButtonPressedEvent -> if (event.window == this.focusedWindow) { this.mouse.onPress(event.button) }
            is MouseButtonReleasedEvent -> if (event.window == this.focusedWindow) { this.mouse.onRelease(event.button) }

            is KeyPressedEvent -> this.keyboard.onPress(event.key)
            is KeyReleasedEvent -> this.keyboard.onRelease(event.key)
            is CharacterEvent -> {}

            is FileDropEvent -> {}
        }
    }
    private fun handleEvents(): Unit {
        this.runtime.pollEvents()
        this.eventManager.compress()
        while (true) {
            val event: Event = this.eventManager.poll() ?: break
            this.handleEvent(event)
            this.eventManager.dispatchEvent(event)

            if (event is WindowCloseRequestEvent) {
                if (event.isCancelled) { event.window.getInternalWindow().setShouldClose(false) }
                else { this.destroyWindow(event.window) }
            }
        }

        this.activeWindows.forEach { it.getInternalWindow().afterEvents() }
    }
    public fun run(): Unit {
        this.start()

        var lastFpsRefreshTimeSeconds: Double = this.context.time.getSecondsSinceStart()
        while(!this.activeWindows.isEmpty()) {
            if (this.context.time.getSecondsSinceStart() - lastFpsRefreshTimeSeconds >= 1.0f) {
                this.fps = 0.0
                lastFpsRefreshTimeSeconds = this.context.time.getSecondsSinceStart()
            }

            this.mouse.cleanup()
            this.keyboard.cleanup()
            this.handleEvents()

            this.focusedWindow?.getUI()?.refreshHoveredElement()
            this.focusedWindow?.setCursor(this.focusedWindow?.getUI()?.getHoveredElement()?.getCursorType())
            this.focusedWindow?.getUI()?.update()

            this.activeWindows.forEach {
                this.renderManager.render(it, it.getUI()!!.getShaders())
            }

            this.activeWindows.forEach { it.handleUIDirtyFlags() }
            this.fps++
        }

        this.shaderManager.releaseAll()
        this.renderManager.free()
    }
}
