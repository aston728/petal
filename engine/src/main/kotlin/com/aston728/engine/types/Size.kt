package com.aston728.engine.types

sealed interface Size<T : Number> {
    val width: T
    val height: T
    val doubleWidth: Double get() = this.width.toDouble()
    val doubleHeight: Double get() = this.height.toDouble()
}
data class IntSize(override val width: Int = 0, override val height: Int = 0) : Size<Int> {
    init {
        require(this.width >= 0 && this.height >= 0) { "width and height must be positive, got (${this.width}, ${this.height})" }
    }

    operator fun plus(other: IntSize): IntSize = IntSize(this.width + other.width, this.height + other.height)
    operator fun minus(other: IntSize): IntSize = IntSize(this.width - other.width, this.height - other.height)
    fun flooredAt(other: IntSize): IntSize = IntSize(maxOf(this.width, other.width), maxOf(this.height, other.height))
    fun ceiledAt(other: IntSize): IntSize = IntSize(minOf(this.width, other.width), minOf(this.height, other.height))
}
data class DecimalSize(override val width: Double = 0.0, override val height: Double = 0.0) : Size<Double> {
    init {
        require(this.width >= 0 && this.height >= 0) { "width and height must be positive, got (${this.width}, ${this.height})" }
    }

    operator fun plus(other: DecimalSize): DecimalSize = DecimalSize(this.width + other.width, this.height + other.height)
    operator fun minus(other: DecimalSize): DecimalSize = DecimalSize(this.width - other.width, this.height - other.height)
    fun flooredAt(other: DecimalSize): DecimalSize = DecimalSize(maxOf(this.width, other.width), maxOf(this.height, other.height))
    fun ceiledAt(other: DecimalSize): DecimalSize = DecimalSize(minOf(this.width, other.width), minOf(this.height, other.height))
}
typealias SizeLike = Size<*>
