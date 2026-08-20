package com.aston728.engine.core

import com.aston728.engine.core.internals.ClipboardService
import com.aston728.engine.core.internals.MonitorService
import com.aston728.engine.core.internals.TimeService
import com.aston728.engine.core.internals.devices.Keyboard
import com.aston728.engine.core.internals.devices.KeyboardController
import com.aston728.engine.core.internals.devices.Mouse
import com.aston728.engine.core.internals.devices.MouseController
import com.aston728.engine.core.rendererBase.ShaderManager
import com.aston728.engine.core.rendererBase.ShaderProvider

public class EngineContext internal constructor(
    public val mouse: Mouse, public val keyboard: Keyboard,
    public val monitors: MonitorService, public val time: TimeService, public val clipboard: ClipboardService,
    public val eventSubscriber: EventSubscriber, public val assetManager: AssetManager,
    internal val shaderProvider: ShaderProvider,
    public val logger: FrozenLogger, public val isDebugOn: Boolean,
)

internal val defaultEngineContext: EngineContext = EngineContext(
    MouseController(), KeyboardController(),
    MonitorService(), TimeService(), ClipboardService(),
    EventManager(), AssetManager(Logger()),
    ShaderManager(),
    Logger().setShowInfo(true).setShowWarnings(true).setShowErrors(true), isDebugOn = false,
)
