package com.aston728.engine.types

sealed interface Offset<T : Number> {
    val x: T
    val y: T
    val doubleX: Double get() = this.x.toDouble()
    val doubleY: Double get() = this.y.toDouble()
}
data class IntOffset(override val x: Int = 0, override val y: Int = 0) : Offset<Int> {
    operator fun plus(other: IntOffset): IntOffset = IntOffset(this.x + other.x, this.y + other.y)
    operator fun minus(other: IntOffset): IntOffset = IntOffset(this.x - other.x, this.y - other.y)
}
data class DecimalOffset(override val x: Double = 0.0, override val y: Double = 0.0) : Offset<Double> {
    operator fun plus(other: DecimalOffset): DecimalOffset = DecimalOffset(this.x + other.x, this.y + other.y)
    operator fun minus(other: DecimalOffset): DecimalOffset = DecimalOffset(this.x - other.x, this.y - other.y)
}
typealias OffsetLike = Offset<*>
