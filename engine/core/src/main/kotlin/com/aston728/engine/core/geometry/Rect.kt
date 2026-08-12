package com.aston728.engine.core.geometry

public class Rect(position: IntPosition = IntPosition(), size: IntSize = IntSize()) {
    public var x: Int = position.x
    public var y: Int = position.y
    public var width: Int = size.width
    public var height: Int = size.height

    public fun getTopLeft(): IntPosition = IntPosition(this.x, this.y)
    public fun getMidTop(): IntPosition = IntPosition(this.x + (this.width / 2), this.y)
    public fun getTopRight(): IntPosition = IntPosition(this.x + this.width, this.y)
    public fun getMidLeft(): IntPosition = IntPosition(this.x, this.y + (this.height / 2))
    public fun getCenter(): IntPosition = IntPosition(this.x + (this.width / 2), this.y + (this.height / 2))
    public fun getMidRight(): IntPosition = IntPosition(this.x + this.width, this.y + (this.height / 2))
    public fun getBottomLeft(): IntPosition = IntPosition(this.x, this.y + this.height)
    public fun getMidBottom(): IntPosition = IntPosition(this.x + (this.width / 2), this.y + this.height)
    public fun getBottomRight(): IntPosition = IntPosition(this.x + this.width, this.y + this.height)
    public fun getSize(): IntSize = IntSize(this.width, this.height)

    public fun setTopLeft(position: IntPosition): Rect = apply {
       this.x = position.x
       this.y = position.y
    }
    public fun setMidTop(position: IntPosition): Rect = apply {
        this.x = position.x - (this.width / 2)
        this.y = position.y
    }
    public fun setTopRight(position: IntPosition): Rect = apply {
        this.x = position.x - this.width
        this.y = position.y
    }
    public fun setMidLeft(position: IntPosition): Rect = apply {
        this.x = position.x
        this.y = position.y - (this.height / 2)
    }
    public fun setCenter(position: IntPosition): Rect = apply {
        this.x = position.x - (this.width / 2)
        this.y = position.y - (this.height / 2)
    }
    public fun setMidRight(position: IntPosition): Rect = apply {
        this.x = position.x - this.width
        this.y = position.y - (this.height / 2)
    }
    public fun setBottomLeft(position: IntPosition): Rect = apply {
        this.x = position.x
        this.y = position.y - this.height
    }
    public fun setMidBottom(position: IntPosition): Rect = apply {
        this.x = position.x - (this.width / 2)
        this.y = position.y - this.height
    }
    public fun setBottomRight(position: IntPosition): Rect = apply {
        this.x = position.x - this.width
        this.y = position.y - this.height
    }
    public fun setSize(size: IntSize): Rect = apply {
        this.width = size.width
        this.height = size.height
    }

    public fun getByCoordinate(coordinate: Coordinate): IntPosition {
        return when (coordinate) {
            Coordinate.TOP_LEFT -> this.getTopLeft()
            Coordinate.MID_TOP -> this.getMidTop()
            Coordinate.TOP_RIGHT -> this.getTopRight()
            Coordinate.MID_LEFT -> this.getMidLeft()
            Coordinate.CENTER -> this.getCenter()
            Coordinate.MID_RIGHT -> this.getMidRight()
            Coordinate.BOTTOM_LEFT -> this.getBottomLeft()
            Coordinate.MID_BOTTOM -> this.getMidBottom()
            Coordinate.BOTTOM_RIGHT -> this.getBottomRight()
        }
    }
    public fun setByCoordinate(coordinate: Coordinate, position: IntPosition): Rect = apply {
        when (coordinate) {
            Coordinate.TOP_LEFT -> this.setTopLeft(position)
            Coordinate.MID_TOP -> this.setMidTop(position)
            Coordinate.TOP_RIGHT -> this.setTopRight(position)
            Coordinate.MID_LEFT -> this.setMidLeft(position)
            Coordinate.CENTER -> this.setCenter(position)
            Coordinate.MID_RIGHT -> this.setMidRight(position)
            Coordinate.BOTTOM_LEFT -> this.setBottomLeft(position)
            Coordinate.MID_BOTTOM -> this.setMidBottom(position)
            Coordinate.BOTTOM_RIGHT -> this.setBottomRight(position)
        }
    }

    public fun isCollidingWith(position: PositionLike): Boolean =
        this.x <= position.getXDouble() && position.getXDouble() < (this.x + this.width) &&
        this.y <= position.getYDouble() && position.getYDouble() < (this.y + this.height)
    public fun getOverlapArea(rect: Rect): Int {
        val overlapWidth: Int = minOf(this.x + this.width, rect.x + rect.width) - maxOf(this.x, rect.x)
        val overlapHeight: Int = minOf(this.y + this.height, rect.y + rect.height) - maxOf(this.y, rect.y)

        if (overlapWidth <= 0 || overlapHeight <= 0) { return 0 }
        return overlapWidth * overlapHeight
    }

    public fun moveBy(offset: IntOffset): Rect = apply {
        this.x += offset.x
        this.y += offset.y
    }

    override fun toString(): String = "Rect(x=${this.x}, y=${this.y}, width=${this.width}, height=${this.height})"
    public fun copy(): Rect = Rect(IntPosition(this.x, this.y), IntSize(this.width, this.height))
}
