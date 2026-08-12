package com.aston728.engine.core

import com.aston728.engine.core.geometry.DecimalOffset
import com.aston728.engine.core.geometry.DecimalPosition
import com.aston728.engine.core.geometry.IntPosition
import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.internals.MonitorInfo
import com.aston728.engine.core.internals.devices.Key
import com.aston728.engine.core.internals.devices.MouseButton

public sealed interface Event
public sealed interface WindowEvent : Event {
    public val window: Window
}

public data class MonitorEvent(public val info: MonitorInfo?, public val isConnected: Boolean) : Event

public data class WindowCloseRequestEvent(override val window: Window) : WindowEvent {
    public var isCancelled: Boolean = false
}
public data class WindowCloseEvent(override val window: Window) : WindowEvent
public data class WindowMoveEvent(override val window: Window, public val position: IntPosition) : WindowEvent
public data class WindowResizeEvent(override val window: Window, public val size: IntSize) : WindowEvent
public data class WindowMinimizeEvent(override val window: Window) : WindowEvent
public data class WindowUnminimizeEvent(override val window: Window) : WindowEvent
public data class WindowMaximizeEvent(override val window: Window) : WindowEvent
public data class WindowUnmaximizeEvent(override val window: Window) : WindowEvent
public data class WindowFocusEvent(override val window: Window) : WindowEvent
public data class WindowUnfocusEvent(override val window: Window) : WindowEvent

public data class MouseEnterEvent(override val window: Window) : WindowEvent
public data class MouseLeaveEvent(override val window: Window) : WindowEvent
public data class MouseMoveEvent(override val window: Window, public val position: DecimalPosition) : WindowEvent
public data class MouseScrollEvent(override val window: Window, public val offset: DecimalOffset) : WindowEvent
public data class MouseButtonPressedEvent(override val window: Window, public val button: MouseButton, public val mods: Int) : WindowEvent
public data class MouseButtonReleasedEvent(override val window: Window, public val button: MouseButton, public val mods: Int) : WindowEvent

public data class KeyPressedEvent(override val window: Window, public val key: Key, public val scanCode: Int, public val mods: Int) : WindowEvent
public data class KeyReleasedEvent(override val window: Window, public val key: Key, public val scanCode: Int, public val mods: Int) : WindowEvent
public data class CharacterEvent(override val window: Window, public val char: Int) : WindowEvent

public data class FileDropEvent(override val window: Window, public val files: List<String>): WindowEvent
