package com.aston728.engine.core

import com.aston728.engine.internals.devices.MouseButton
import com.aston728.engine.internals.devices.Key

import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.DecimalPosition
import com.aston728.engine.types.DecimalOffset
import com.aston728.engine.types.IntSize

sealed interface Event {
    val window: Window
}

data class WindowCloseRequestEvent(override val window: Window) : Event {
    var isCancelled: Boolean = false
        private set
    fun cancel(): Unit { this.isCancelled = true }
}
data class WindowCloseEvent(override val window: Window) : Event
data class WindowMoveEvent(override val window: Window, val position: IntPosition) : Event
data class WindowResizeEvent(override val window: Window, val size: IntSize) : Event
data class WindowMinimizeEvent(override val window: Window) : Event
data class WindowUnminimizeEvent(override val window: Window) : Event
data class WindowMaximizeEvent(override val window: Window) : Event
data class WindowUnmaximizeEvent(override val window: Window) : Event
data class WindowFocusEvent(override val window: Window) : Event
data class WindowUnfocusEvent(override val window: Window) : Event

data class MouseEnterEvent(override val window: Window) : Event
data class MouseLeaveEvent(override val window: Window) : Event
data class MouseMoveEvent(override val window: Window, val position: DecimalPosition) : Event
data class MouseScrollEvent(override val window: Window, val offset: DecimalOffset) : Event
data class MouseButtonPressedEvent(override val window: Window, val button: MouseButton, val mods: Int) : Event
data class MouseButtonReleasedEvent(override val window: Window, val button: MouseButton, val mods: Int) : Event

data class KeyPressedEvent(override val window: Window, val key: Key, val scanCode: Int, val mods: Int) : Event
data class KeyReleasedEvent(override val window: Window, val key: Key, val scanCode: Int, val mods: Int) : Event
data class CharacterEvent(override val window: Window, val char: Int) : Event
