package com.aston728.engine.core.elementBase.layout

import com.aston728.engine.core.geometry.Coordinate
import com.aston728.engine.core.geometry.IntPosition
import com.aston728.engine.core.geometry.Rect
import com.aston728.engine.core.types.GenericUIElement

public enum class AnchorType { ELEMENT, WINDOW, PARENT }

public class Anchor private constructor(
    private val target: GenericUIElement?, private val coordinate: Coordinate,
    private val type: AnchorType, private var rect: Rect
) {
    public companion object {
        public fun toElement(element: GenericUIElement, coordinate: Coordinate): Anchor = Anchor(element, coordinate, AnchorType.ELEMENT, element.getRectUnsafe())
        public fun toWindow(coordinate: Coordinate): Anchor = Anchor(null, coordinate, AnchorType.WINDOW, Rect())
        public fun toParent(coordinate: Coordinate): Anchor = Anchor(null, coordinate, AnchorType.PARENT, Rect())
    }

    public fun getTarget(): GenericUIElement? = this.target
    public fun getCoordinate(): Coordinate = this.coordinate
    public fun getType(): AnchorType = this.type
    public fun getDepth(): Int {
        var depth: Int = 0
        var target: GenericUIElement? = this.target
        while (target != null) {
            target = target.getPositionUnsafe().getAnchorUnsafe().getTarget()
            depth++
        }

        return depth
    }

    internal fun setRect(rect: Rect): Anchor = apply {
        this.rect = rect
    }
    internal fun resolvePosition(): IntPosition = this.rect.getByCoordinate(this.coordinate)

    override fun toString(): String = "Anchor(target=${this.target}, coordinate=${this.coordinate}, type=${this.type}, rect=${this.rect})"
    public fun copy(): Anchor = Anchor(this.target, this.coordinate, this.type, this.rect.copy())
}
