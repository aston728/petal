package com.aston728.engine.core

import com.aston728.engine.core.blankRenderer.BlankRenderManager
import com.aston728.engine.core.internals.BackendInfo
import com.aston728.engine.core.internals.ClipboardService
import com.aston728.engine.core.internals.MonitorService
import com.aston728.engine.core.internals.TimeService
import com.aston728.engine.core.internals.devices.Keyboard
import com.aston728.engine.core.internals.devices.KeyboardController
import com.aston728.engine.core.internals.devices.Mouse
import com.aston728.engine.core.internals.devices.MouseController
import com.aston728.engine.core.rendererBase.RenderManager

public class EngineContext internal constructor(
    public val mouse: Mouse, public val keyboard: Keyboard,
    public val monitors: MonitorService, public val time: TimeService, public val clipboard: ClipboardService,
    public val eventSubscriber: EventSubscriber, public val assetLoader: AssetLoader,
    public val backendInfo: BackendInfo, public val renderManager: RenderManager, public val isDebugOn: Boolean,
    public val logger: FrozenLogger,
)

internal val DEFAULT_LOGGER: FrozenLogger = Logger().setShowInfo(true).setShowWarnings(true).setShowErrors(true)
internal const val DEFAULT_IS_DEBUG_ON: Boolean = false
internal val DEFAULT_ENGINE_CONTEXT: EngineContext = EngineContext(
    MouseController(), KeyboardController(),
    MonitorService(), TimeService(), ClipboardService(),
    EventManager(), AssetLoader(DEFAULT_LOGGER),
    BackendInfo(), BlankRenderManager(), DEFAULT_IS_DEBUG_ON,
    DEFAULT_LOGGER,
)
