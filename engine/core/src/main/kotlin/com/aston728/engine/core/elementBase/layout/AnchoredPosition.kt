package com.aston728.engine.core.elementBase.layout

import com.aston728.engine.core.geometry.Coordinate
import com.aston728.engine.core.geometry.IntOffset
import com.aston728.engine.core.geometry.Rect

public class AnchoredPosition(private var anchor: Anchor, private var coordinate: Coordinate, private var offset: IntOffset) {
    private var desiredAnchor: Anchor = this.anchor

    internal fun getAnchorUnsafe(): Anchor = this.anchor
    internal fun getDesiredAnchorUnsafe(): Anchor = this.desiredAnchor

    public fun getAnchor(): Anchor = this.anchor.copy()
    public fun getCoordinate(): Coordinate = this.coordinate
    public fun getOffset(): IntOffset = this.offset

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
    public fun copy(): AnchoredPosition = AnchoredPosition(this.anchor.copy(), this.coordinate, this.offset)
}
