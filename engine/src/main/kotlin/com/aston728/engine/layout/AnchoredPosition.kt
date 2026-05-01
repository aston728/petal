package com.aston728.engine.layout

import com.aston728.engine.types.IntOffset

class AnchoredPosition(
    private var anchor: Anchor,
    private var coordinate: Coordinate, private var offset: IntOffset
) {
    private var desiredAnchor: Anchor = this.anchor

    internal fun getAnchorUnsafe(): Anchor = this.anchor
    internal fun getDesiredAnchorUnsafe(): Anchor = this.desiredAnchor

    fun getAnchor(): Anchor = this.anchor.copy()
    fun getCoordinate(): Coordinate = this.coordinate
    fun getOffset(): IntOffset = this.offset

    internal fun setAnchor(anchor: Anchor): AnchoredPosition = apply {
        this.desiredAnchor = anchor
    }
    internal fun setCoordinate(coordinate: Coordinate): AnchoredPosition = apply {
        this.coordinate = coordinate
    }
    internal fun setOffset(offset: IntOffset): AnchoredPosition = apply {
        this.offset = offset
    }

    internal fun commitAnchorChange(): Unit {
        this.anchor = this.desiredAnchor
    }
    internal fun applyTo(rect: Rect): Unit {
        rect
            .setByCoordinate(this.coordinate, this.anchor.resolvePosition())
            .moveBy(this.offset)
    }

    override fun toString(): String = "AnchoredPosition(anchor=${this.anchor}, coordinate=${this.coordinate}, offset=${this.offset})"
    fun copy(): AnchoredPosition = AnchoredPosition(
        this.anchor.copy(),
        this.coordinate, this.offset
    )
}
