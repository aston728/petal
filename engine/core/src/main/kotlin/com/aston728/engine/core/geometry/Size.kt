package com.aston728.engine.core.geometry

public sealed interface Size<T : Number> {
    public val width: T
    public val height: T
    public fun getWidthDouble(): Double = this.width.toDouble()
    public fun getHeightDouble(): Double = this.height.toDouble()
}
public data class IntSize(override val width: Int = 0, override val height: Int = 0) : Size<Int> {
    init {
        require(this.width >= 0 && this.height >= 0) { "Width and height must be positive, got (${this.width}, ${this.height})" }
    }

    public operator fun plus(other: IntSize): IntSize = IntSize(this.width + other.width, this.height + other.height)
    public operator fun minus(other: IntSize): IntSize = IntSize(this.width - other.width, this.height - other.height)
    public fun flooredAt(other: IntSize): IntSize = IntSize(maxOf(this.width, other.width), maxOf(this.height, other.height))
    public fun ceiledAt(other: IntSize): IntSize = IntSize(minOf(this.width, other.width), minOf(this.height, other.height))
}
public data class DecimalSize(override val width: Double = 0.0, override val height: Double = 0.0) : Size<Double> {
    init {
        require(this.width >= 0 && this.height >= 0) { "Width and height must be positive, got (${this.width}, ${this.height})" }
    }

    public operator fun plus(other: DecimalSize): DecimalSize = DecimalSize(this.width + other.width, this.height + other.height)
    public operator fun minus(other: DecimalSize): DecimalSize = DecimalSize(this.width - other.width, this.height - other.height)
    public fun flooredAt(other: DecimalSize): DecimalSize = DecimalSize(maxOf(this.width, other.width), maxOf(this.height, other.height))
    public fun ceiledAt(other: DecimalSize): DecimalSize = DecimalSize(minOf(this.width, other.width), minOf(this.height, other.height))
}
public typealias SizeLike = Size<*>
