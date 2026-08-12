package com.aston728.engine.core.internals.devices

import com.aston728.engine.core.geometry.DecimalPosition
import com.aston728.engine.core.geometry.DecimalOffset

public interface Mouse {
    public fun getPosition(): DecimalPosition
    public fun getScrollOffset(): DecimalOffset
    public fun isPressed(button: MouseButton): Boolean
    public fun isJustPressed(button: MouseButton): Boolean
    public fun isJustReleased(button: MouseButton): Boolean
}
