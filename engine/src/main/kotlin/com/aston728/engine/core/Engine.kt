package com.aston728.engine.core

import com.aston728.engine.renderer.Renderer
import com.aston728.engine.renderer.GraphicsApi
import com.aston728.engine.renderer.BlankRenderer

import com.aston728.engine.utils.centerString

import com.aston728.engine.internals.core.EngineRuntime
import com.aston728.engine.internals.core.MonitorService
import com.aston728.engine.internals.core.ClipboardService
import com.aston728.engine.internals.core.TimeService

import com.aston728.engine.internals.devices.MouseController
import com.aston728.engine.internals.devices.KeyboardController
import com.aston728.engine.internals.devices.Key

import com.aston728.engine.internals.renderer.OpenGLRenderer
import com.aston728.engine.internals.renderer.VulkanRenderer

class Engine private constructor(config: EngineConfig) {
    companion object {
        private var instance: Engine? = null
        fun create(config: EngineConfig): Engine = synchronized(this) {
            if (this.instance != null) {
                this.instance!!.getContext().logger.error("ENGINE", "Cannot create more than one engine instance")
            } else {
                this.instance = Engine(config)
            }
            return this.instance!!
        }
    }

    private val mode: EngineMode = config.engineMode
    private val graphicsApi: GraphicsApi = config.graphicsApi

    private val windows: MutableSet<Window> = mutableSetOf()
    private var focusedWindow: Window? = null

    private var stopKey: Key? = Key.ESCAPE
    private var resetKey: Key? = Key.F1
    private var fullscreenKey: Key? = Key.F11

    private var fps: Double = 0.0
    private var fpsCap: Int? = null
    private var didStart: Boolean = false

    private val runtime: EngineRuntime = EngineRuntime()
    private val mouse: MouseController = MouseController()
    private val keyboard: KeyboardController = KeyboardController()
    private val eventManager: EventManager = EventManager()
    private val context: EngineContext = this.createContext()

    private val renderer: Renderer = when (this.graphicsApi) {
        GraphicsApi.OPEN_GL -> OpenGLRenderer()
        GraphicsApi.VULKAN -> VulkanRenderer()
        GraphicsApi.NONE -> BlankRenderer()
    }

    init {
        this.context.eventSubscriber.subscribe(KeyPressedEvent::class, { event -> when(event.key) {
            this.stopKey -> this.stop()
            this.resetKey -> this.focusedWindow?.reset()
            this.fullscreenKey -> this.focusedWindow?.toggleFullscreen()
        }})
    }

    private fun createContext(): EngineContext {
        val clipboard: ClipboardService = ClipboardService(
            windowProvider = {
                val window: Window? = this.focusedWindow ?: this.windows.firstOrNull()
                window?.getInternalWindow()?.getHandle()
            },
            onFailure = { message -> this.context.logger.error("CLIPBOARD", message) }
        )
        val logger: Logger = Logger()
            .setFormat { severity: String, title: String, message: String ->
                val topDivider: String = centerString(severity, '-', 100)
                val bottomDivider: String = "-".repeat(100)
                "$topDivider\n$title\n$message\n$bottomDivider"
            }
            .setHandler { message -> System.err.println(message) }
        when (this.mode) {
            EngineMode.RELEASE -> logger.setShowInfo(false).setShowWarnings(false).setShowErrors(false)
            EngineMode.DEVELOPMENT, EngineMode.DEBUG -> logger.setShowInfo(true).setShowWarnings(true).setShowErrors(true)
        }

        return EngineContext(
            this.mouse, this.keyboard,
            MonitorService(), TimeService(), clipboard,
            this.eventManager,
            logger, isDebugOn = (this.mode == EngineMode.DEBUG),
        )
    }

    fun init(): Unit {
        val didSucceed: Boolean = this.runtime.start(errorCallback = { code, description ->
            this.context.logger.error("RUNTIME", "Internal error ($code): $description")
        })
        if (!didSucceed) { kotlin.system.exitProcess(1) }
    }
    fun shutdown(): Unit { this.runtime.stop() }

    fun getMode(): EngineMode = this.mode
    fun getGraphicsApi(): GraphicsApi = this.graphicsApi
    fun getFocusedWindow(): Window? = this.focusedWindow
    fun getStopKey(): Key? = this.stopKey
    fun getResetKey(): Key? = this.resetKey
    fun getFullscreenKey(): Key? = this.fullscreenKey
    fun getFps(): Double = this.fps
    fun getFpsCap(): Int? = this.fpsCap
    fun getContext(): EngineContext = this.context

    fun setFocusedWindow(window: Window): Engine = apply {
        if (window.getState() != WindowState.ALIVE) {
            this.context.logger.error("ENGINE", "Cannot focus a non-alive window: $window")
        } else {
            if (window !in this.windows) {
                this.context.logger.warn("ENGINE", "Focused an absent window: $window")
                this.addWindow(window)
            }
            window.focus()
        }
    }
    fun setStopKey(key: Key?): Engine = apply {
        this.stopKey = key
    }
    fun setResetKey(key: Key?): Engine = apply {
        this.resetKey = key
    }
    fun setFullscreenKey(key: Key?): Engine = apply {
        this.fullscreenKey = key
    }
    fun setFpsCap(cap: Int?): Engine = apply {
        var cap: Int? = cap
        if (cap != null && cap < 1) {
            this.context.logger.warn("ENGINE", "Invalid FPS cap: $cap")
            cap = 30
        }
        this.fpsCap = cap
    }

    fun createWindow(): Window = Window(
        this.context,
        this.graphicsApi, debugMessageCallback = { message -> this.context.logger.error("RUNTIME (DEBUG)", message) }
    )
    fun addWindow(window: Window): Engine = apply {
        if (window.getState() != WindowState.ALIVE) {
            this.context.logger.error("ENGINE", "Cannot add a non-alive window: $window")
        } else {
            val didAdd: Boolean = this.windows.add(window)
            if (!didAdd) {
                this.context.logger.warn("ENGINE", "Added an already present window: $window")
            }
            window.attachContext(this.context)
            this.eventManager.registerWindow(window)

            if (this.didStart) {
                this.eventManager.addEvent(WindowResizeEvent(window, window.getSizeUnsafe()))  // not triggered automatically
                window.show().focus()
            }
        }
    }
    fun removeWindow(window: Window): Engine = apply {
        if (window.getState() != WindowState.ALIVE) {
            this.context.logger.error("ENGINE", "Cannot remove a non-alive window: $window")
        } else {
            if (this.focusedWindow == window) { this.focusedWindow = null }

            window.hide()
            val didRemove: Boolean = this.windows.remove(window)
            if (!didRemove) {
                this.context.logger.warn("ENGINE", "Removed an already absent window: $window")
            }
        }
    }
    fun destroyWindow(window: Window): Engine = apply {
        if (window.getState() != WindowState.ALIVE) {
            this.context.logger.error("ENGINE", "Cannot destroy a non-alive window: $window")
        } else {
            if (this.focusedWindow == window) { this.focusedWindow = null }

            window.startClosing()
            this.eventManager.addEvent(WindowCloseEvent(window))
            this.windows.remove(window)
        }
    }
    fun stop(): Engine = apply {
        this.windows.toList().forEach { this.destroyWindow(it) }
    }
    fun setStructure(vararg windows: Window): Engine = apply {
        this.windows.toList().forEach { this.removeWindow(it) }
        windows.toList().forEach { this.addWindow(it) }
    }

    internal fun start(): Unit {
        this.didStart = true
        this.windows.forEach {
            this.eventManager.addEvent(WindowResizeEvent(it, it.getSizeUnsafe())) // not triggered automatically
            it.show().focus()
        }
    }
    internal fun handleEvent(event: Event): Unit {
        when (event) {
            is WindowCloseRequestEvent -> { event.window.onCloseRequest() }
            is WindowCloseEvent -> { event.window.onClose() }
            is WindowMoveEvent -> event.window.onMove(event.position)
            is WindowResizeEvent -> event.window.onResize(event.size)
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
        }
    }
    internal fun handleEvents(): Unit {
        this.runtime.pollEvents()
        this.eventManager.compress()
        while (true) {
            val event: Event = this.eventManager.poll() ?: break
            this.handleEvent(event)
            this.eventManager.dispatchEvent(event)

            if (event is WindowCloseRequestEvent) {
                if (event.isCancelled) { event.window.setShouldClose(false) }
                else { this.destroyWindow(event.window) }
            }
        }
    }
    fun run(): Unit {
        this.start()

        var lastFpsRefreshTimeSeconds: Double = this.context.time.getSecondsSinceStart()
        while(!this.windows.isEmpty()) {
            if (this.context.time.getSecondsSinceStart() - lastFpsRefreshTimeSeconds >= 1.0f) {
                //println("FPS: ${this.fps}")
                this.fps = 0.0
                lastFpsRefreshTimeSeconds = this.context.time.getSecondsSinceStart()
            }

            this.mouse.cleanup()
            this.keyboard.cleanup()
            this.handleEvents()

            this.focusedWindow?.getUI()?.update()
            this.windows.forEach { it.handleUIDirtyFlags() }

            this.focusedWindow?.getUI()?.refreshHoveredElement()
            this.focusedWindow?.setCursor(this.focusedWindow?.getUI()?.getHoveredElement()?.getCursorType())

            this.windows.forEach {
                it.getGraphicsContext().startDrawing()
                it.getUI()?.draw(this.renderer)
                it.getGraphicsContext().stopDrawing()
            }

            this.fps++
        }
    }
}
