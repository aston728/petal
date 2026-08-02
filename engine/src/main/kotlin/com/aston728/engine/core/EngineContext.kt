package com.aston728.engine.core

import com.aston728.engine.renderer.ShaderManager
import com.aston728.engine.renderer.ShaderProvider

import com.aston728.engine.internals.core.MonitorService
import com.aston728.engine.internals.core.ClipboardService
import com.aston728.engine.internals.core.TimeService

import com.aston728.engine.internals.devices.MouseController
import com.aston728.engine.internals.devices.KeyboardController
import com.aston728.engine.internals.devices.Mouse
import com.aston728.engine.internals.devices.Keyboard

class EngineContext internal constructor(
    val mouse: Mouse, val keyboard: Keyboard,
    val monitors: MonitorService, val time: TimeService, val clipboard: ClipboardService,
    val eventSubscriber: EventSubscriber, internal val shaderProvider: ShaderProvider,
    val logger: FrozenLogger, val isDebugOn: Boolean,
)

private val defaultLogger: Logger = Logger()
    .setShowInfo(true)
    .setShowWarnings(true)
    .setShowErrors(true)
internal val defaultEngineContext: EngineContext = EngineContext(
    MouseController(), KeyboardController(),
    MonitorService(), TimeService(), ClipboardService(),
    EventManager(), ShaderManager(),
    defaultLogger, isDebugOn = false,
)
