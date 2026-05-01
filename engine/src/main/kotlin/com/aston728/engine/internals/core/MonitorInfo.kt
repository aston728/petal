package com.aston728.engine.internals.core

import com.aston728.engine.layout.Rect

class MonitorInfo internal constructor(
    private val handle: Long, private val name: String,
    private val rect: Rect, private val usableRect: Rect,
    private val refreshRate: Int
) {
    internal fun getHandle(): Long = this.handle

    fun getName(): String = this.name
    fun getRect(): Rect = this.rect.copy()
    fun getUsableRect(): Rect = this.usableRect.copy()
    fun getRefreshRate(): Int = this.refreshRate

    override fun toString(): String = (
        "MonitorInfo(name='${this.name}', rect=${this.rect}, usableRect=${this.usableRect}, refreshRate=${this.refreshRate})"
    )
}
