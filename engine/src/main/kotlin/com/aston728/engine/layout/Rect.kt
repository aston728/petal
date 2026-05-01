package com.aston728.engine.layout

import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.PositionLike
import com.aston728.engine.types.IntOffset
import com.aston728.engine.types.IntSize

class Rect(position: IntPosition = IntPosition(), size: IntSize = IntSize()) {
    var x: Int = position.x
        private set
    var y: Int = position.y
        private set
    var width: Int = size.width
        private set
    var height: Int = size.height
        private set

    fun getTopLeft(): IntPosition = IntPosition(this.x, this.y)
    fun getMidTop(): IntPosition = IntPosition(this.x + (this.width / 2), this.y)
    fun getTopRight(): IntPosition = IntPosition(this.x + this.width, this.y)
    fun getMidLeft(): IntPosition = IntPosition(this.x, this.y + (this.height / 2))
    fun getCenter(): IntPosition = IntPosition(this.x + (this.width / 2), this.y + (this.height / 2))
    fun getMidRight(): IntPosition = IntPosition(this.x + this.width, this.y + (this.height / 2))
    fun getBottomLeft(): IntPosition = IntPosition(this.x, this.y + this.height)
    fun getMidBottom(): IntPosition = IntPosition(this.x + (this.width / 2), this.y + this.height)
    fun getBottomRight(): IntPosition = IntPosition(this.x + this.width, this.y + this.height)
    fun getSize(): IntSize = IntSize(this.width, this.height)

    fun setTopLeft(position: IntPosition): Rect = apply {
       this.x = position.x
       this.y = position.y
    }
    fun setMidTop(position: IntPosition): Rect = apply {
        this.x = position.x - (this.width / 2)
        this.y = position.y
    }
    fun setTopRight(position: IntPosition): Rect = apply {
        this.x = position.x - this.width
        this.y = position.y
    }
    fun setMidLeft(position: IntPosition): Rect = apply {
        this.x = position.x
        this.y = position.y - (this.height / 2)
    }
    fun setCenter(position: IntPosition): Rect = apply {
        this.x = position.x - (this.width / 2)
        this.y = position.y - (this.height / 2)
    }
    fun setMidRight(position: IntPosition): Rect = apply {
        this.x = position.x - this.width
        this.y = position.y - (this.height / 2)
    }
    fun setBottomLeft(position: IntPosition): Rect = apply {
        this.x = position.x
        this.y = position.y - this.height
    }
    fun setMidBottom(position: IntPosition): Rect = apply {
        this.x = position.x - (this.width / 2)
        this.y = position.y - this.height
    }
    fun setBottomRight(position: IntPosition): Rect = apply {
        this.x = position.x - this.width
        this.y = position.y - this.height
    }
    fun setSize(size: IntSize): Rect = apply {
        this.width = size.width
        this.height = size.height
    }

    fun getByCoordinate(coordinate: Coordinate): IntPosition {
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
    fun setByCoordinate(coordinate: Coordinate, position: IntPosition): Rect = apply {
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

    fun isCollidingWith(position: PositionLike): Boolean =
        this.x <= position.doubleX && position.doubleX < (this.x + this.width) &&
        this.y <= position.doubleY && position.doubleY < (this.y + this.height)
    fun getOverlapArea(rect: Rect): Int {
        val overlapWidth: Int = minOf(this.x + this.width, rect.x + rect.width) - maxOf(this.x, rect.x)
        val overlapHeight: Int = minOf(this.y + this.height, rect.y + rect.height) - maxOf(this.y, rect.y)

        if (overlapWidth <= 0 || overlapHeight <= 0) { return 0 }
        return overlapWidth * overlapHeight
    }

    fun moveBy(offset: IntOffset): Rect = apply {
        this.x += offset.x
        this.y += offset.y
    }

    override fun toString(): String = "Rect(x=${this.x}, y=${this.y}, width=${this.width}, height=${this.height})"
    fun copy(): Rect = Rect(IntPosition(this.x, this.y), IntSize(this.width, this.height))
}
