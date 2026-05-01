package com.aston728.engine.core

import kotlin.reflect.KClass

import com.aston728.engine.utils.Handle

import com.aston728.engine.internals.devices.DeviceAction
import com.aston728.engine.internals.devices.MouseButton
import com.aston728.engine.internals.devices.Key

import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.DecimalPosition
import com.aston728.engine.types.DecimalOffset
import com.aston728.engine.types.IntSize
import com.aston728.engine.types.EventHandler

internal class EventManager : EventSubscriber {
    private val events: ArrayDeque<Event> = ArrayDeque()
    private val eventMappings: MutableMap<KClass<out Event>, MutableList<EventHandler>> = mutableMapOf()

    private fun handleWindowMinimizeChangeEvent(window: Window, isMinimized: Boolean): Unit {
        if (isMinimized) { this.addEvent(WindowMinimizeEvent(window)) }
        else { this.addEvent(WindowUnminimizeEvent(window)) }
    }
    private fun handleWindowMaximizeChangeEvent(window: Window, isMaximized: Boolean): Unit {
        if (isMaximized) { this.addEvent(WindowMaximizeEvent(window)) }
        else { this.addEvent(WindowUnmaximizeEvent(window)) }
    }
    private fun handleWindowFocusChangeEvent(window: Window, isFocused: Boolean): Unit {
        if (isFocused) { this.addEvent(WindowFocusEvent(window)) }
        else { this.addEvent(WindowUnfocusEvent(window)) }
    }
    private fun handleMouseEnterLeaveEvent(window: Window, didEnter: Boolean): Unit {
        if (didEnter) { this.addEvent(MouseEnterEvent(window)) }
        else { this.addEvent(MouseLeaveEvent(window)) }
    }
    private fun handleMouseButtonEvent(window: Window, button: Int, action: Int, mods: Int): Unit {
        when (DeviceAction.fromInt(action)) {
            DeviceAction.PRESSED -> this.addEvent(MouseButtonPressedEvent(window, MouseButton.fromInt(button), mods))
            DeviceAction.RELEASE -> this.addEvent(MouseButtonReleasedEvent(window, MouseButton.fromInt(button), mods))
            DeviceAction.REPEAT -> {}
        }
    }
    private fun handleKeyEvent(window: Window, key: Int, scanCode: Int, action: Int, mods: Int): Unit {
        when (DeviceAction.fromInt(action)) {
            DeviceAction.PRESSED -> this.addEvent(KeyPressedEvent(window, Key(key), scanCode, mods))
            DeviceAction.RELEASE -> this.addEvent(KeyReleasedEvent(window, Key(key), scanCode, mods))
            DeviceAction.REPEAT -> {}
        }
    }
    internal fun registerWindow(window: Window): EventManager = apply {
        window.getInternalWindow()
            .setOnCloseRequestCallback { _ -> this.addEvent(WindowCloseRequestEvent(window)) }
            .setOnMoveCallback { _, x, y -> this.addEvent(WindowMoveEvent(window, IntPosition(x, y))) }
            .setOnResizeCallback { _, width, height -> this.addEvent(WindowResizeEvent(window, IntSize(width, height))) }
            .setOnMinimizeChangeCallback { _, isMinimized -> this.handleWindowMinimizeChangeEvent(window, isMinimized) }
            .setOnMaximizeChangeCallback { _, isMaximized -> this.handleWindowMaximizeChangeEvent(window, isMaximized) }
            .setOnFocusChangeCallback { _, isFocused -> this.handleWindowFocusChangeEvent(window, isFocused) }

            .setOnMouseEnterLeaveCallback { _, didEnter -> this.handleMouseEnterLeaveEvent(window, didEnter) }
            .setOnMouseMoveCallback { _, x, y -> this.addEvent(MouseMoveEvent(window, DecimalPosition(x, y))) }
            .setOnMouseScrollCallback { _, x, y -> this.addEvent(MouseScrollEvent(window, DecimalOffset(x, y))) }
            .setOnMouseButtonCallback { _, button, action, mods -> this.handleMouseButtonEvent(window, button, action, mods) }

            .setOnKeyCallback { _, key, scanCode, action, mods -> this.handleKeyEvent(window, key, scanCode, action, mods) }
            .setOnCharacterCallback { _, char -> this.addEvent(CharacterEvent(window, char)) }
    }

    internal fun compress(): Unit {
        val windowMoveEventMap: MutableMap<Window, WindowMoveEvent> = mutableMapOf()
        val windowResizeEventMap: MutableMap<Window, WindowResizeEvent> = mutableMapOf()
        this.events.forEach {
            when (it) {
                is WindowMoveEvent -> windowMoveEventMap[it.window] = it
                is WindowResizeEvent -> windowResizeEventMap[it.window] = it
                else -> {}
            }
        }
        this.events.retainAll {
            when (it) {
                is WindowMoveEvent -> it === windowMoveEventMap[it.window]
                is WindowResizeEvent -> it === windowResizeEventMap[it.window]
                else -> true
            }
        }
    }

    internal fun poll(): Event? = this.events.removeFirstOrNull()
    internal fun addEvent(event: Event): EventManager = apply {
        this.events.add(event)
    }

    internal fun dispatchEvent(event: Event): EventManager = apply {
        this.eventMappings[event::class]?.toList()?.forEach { it(event) }
    }
    override fun <T : Event> subscribe(event: KClass<T>, handler: (T) -> Unit, handle: Handle?): EventManager = apply {
        val handlers: MutableList<EventHandler> = this.eventMappings.getOrPut(event) { mutableListOf() }
        @Suppress("unchecked_cast")
        val wrapper: EventHandler = { event -> handler(event as T) }

        handlers.add(wrapper)
        handle?.setOnRemoveHandler { handlers.remove(wrapper) }
    }
}
