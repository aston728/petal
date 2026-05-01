package com.aston728.engine.layout

import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.GenericUIElement

class Anchor private constructor(
    private val target: GenericUIElement?, private val coordinate: Coordinate,
    private val type: AnchorType, private var rect: Rect
) {
    companion object {
        fun toElement(element: GenericUIElement, coordinate: Coordinate): Anchor = Anchor(
            element, coordinate, AnchorType.ELEMENT, element.getRectUnsafe()
        )
        fun toWindow(coordinate: Coordinate): Anchor = Anchor(null, coordinate, AnchorType.WINDOW, Rect())
        fun toParent(coordinate: Coordinate): Anchor = Anchor(null, coordinate, AnchorType.PARENT, Rect())
    }

    fun getTarget(): GenericUIElement? = this.target
    fun getCoordinate(): Coordinate = this.coordinate
    fun getDepth(): Int {
        var depth: Int = 0
        var target: GenericUIElement? = this.target
        while (target != null) {
            target = target.getPositionUnsafe().getAnchorUnsafe().getTarget()
            depth++
        }

        return depth
    }
    fun isElementAnchor(): Boolean = this.type == AnchorType.ELEMENT
    fun isWindowAnchor(): Boolean = this.type == AnchorType.WINDOW
    fun isParentAnchor(): Boolean = this.type == AnchorType.PARENT

    internal fun setRect(rect: Rect): Anchor = apply {
        this.rect = rect
    }
    internal fun resolvePosition(): IntPosition = this.rect.getByCoordinate(this.coordinate)

    override fun toString(): String = "Anchor(target=${this.target}, coordinate=${this.coordinate}, type=${this.type}, rect=${this.rect})"
    fun copy(): Anchor = Anchor(this.target, this.coordinate, this.type, this.rect.copy())
}
