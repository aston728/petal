package com.aston728.engine.core

import com.aston728.engine.internals.core.MonitorInfo
import com.aston728.engine.internals.devices.MouseButton
import com.aston728.engine.internals.devices.Key

import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.DecimalPosition
import com.aston728.engine.types.DecimalOffset
import com.aston728.engine.types.IntSize

sealed interface Event
sealed interface WindowEvent : Event {
    val window: Window
}

data class MonitorEvent(val info: MonitorInfo?, val isConnected: Boolean) : Event

data class WindowCloseRequestEvent(override val window: Window) : WindowEvent {
    var isCancelled: Boolean = false
        private set
    fun cancel(): Unit { this.isCancelled = true }
}
data class WindowCloseEvent(override val window: Window) : WindowEvent
data class WindowMoveEvent(override val window: Window, val position: IntPosition) : WindowEvent
data class WindowResizeEvent(override val window: Window, val size: IntSize) : WindowEvent
data class WindowMinimizeEvent(override val window: Window) : WindowEvent
data class WindowUnminimizeEvent(override val window: Window) : WindowEvent
data class WindowMaximizeEvent(override val window: Window) : WindowEvent
data class WindowUnmaximizeEvent(override val window: Window) : WindowEvent
data class WindowFocusEvent(override val window: Window) : WindowEvent
data class WindowUnfocusEvent(override val window: Window) : WindowEvent

data class MouseEnterEvent(override val window: Window) : WindowEvent
data class MouseLeaveEvent(override val window: Window) : WindowEvent
data class MouseMoveEvent(override val window: Window, val position: DecimalPosition) : WindowEvent
data class MouseScrollEvent(override val window: Window, val offset: DecimalOffset) : WindowEvent
data class MouseButtonPressedEvent(override val window: Window, val button: MouseButton, val mods: Int) : WindowEvent
data class MouseButtonReleasedEvent(override val window: Window, val button: MouseButton, val mods: Int) : WindowEvent

data class KeyPressedEvent(override val window: Window, val key: Key, val scanCode: Int, val mods: Int) : WindowEvent
data class KeyReleasedEvent(override val window: Window, val key: Key, val scanCode: Int, val mods: Int) : WindowEvent
data class CharacterEvent(override val window: Window, val char: Int) : WindowEvent

data class FileDropEvent(override val window: Window, val numFiles: Int, val files: List<String>): WindowEvent
