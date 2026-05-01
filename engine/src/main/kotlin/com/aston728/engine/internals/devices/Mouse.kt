package com.aston728.engine.internals.devices

import com.aston728.engine.types.DecimalPosition
import com.aston728.engine.types.DecimalOffset

interface Mouse {
    fun getPosition(): DecimalPosition
    fun getScrollOffset(): DecimalOffset
    fun isPressed(button: MouseButton): Boolean
    fun isJustPressed(button: MouseButton): Boolean
    fun isJustReleased(button: MouseButton): Boolean
}
