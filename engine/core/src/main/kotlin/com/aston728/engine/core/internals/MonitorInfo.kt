package com.aston728.engine.core.internals

import com.aston728.engine.core.geometry.Rect

public class MonitorInfo internal constructor(
    private val handle: Long, private val name: String,
    private val rect: Rect, private val usableRect: Rect,
    private val refreshRate: Int
) {
    internal fun getHandle(): Long = this.handle

    public fun getName(): String = this.name
    public fun getRect(): Rect = this.rect.copy()
    public fun getUsableRect(): Rect = this.usableRect.copy()
    public fun getRefreshRate(): Int = this.refreshRate

    override fun toString(): String = "MonitorInfo(name='${this.name}', rect=${this.rect}, usableRect=${this.usableRect}, refreshRate=${this.refreshRate})"
}
