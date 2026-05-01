package com.aston728.engine.types

sealed interface Position<T : Number> {
    val x: T
    val y: T
    val doubleX: Double get() = this.x.toDouble()
    val doubleY: Double get() = this.y.toDouble()
}
data class IntPosition(override val x: Int = 0, override val y: Int = 0) : Position<Int> {
    operator fun plus(other: IntPosition): IntPosition = IntPosition(this.x + other.x, this.y + other.y)
    operator fun plus(other: IntOffset): IntPosition = IntPosition(this.x + other.x, this.y + other.y)
    operator fun minus(other: IntPosition): IntPosition = IntPosition(this.x - other.x, this.y - other.y)
    operator fun minus(other: IntOffset): IntPosition = IntPosition(this.x - other.x, this.y - other.y)
}
data class DecimalPosition(override val x: Double = 0.0, override val y: Double = 0.0) : Position<Double> {
    operator fun plus(other: DecimalPosition): DecimalPosition = DecimalPosition(this.x + other.x, this.y + other.y)
    operator fun plus(other: DecimalOffset): DecimalPosition = DecimalPosition(this.x + other.x, this.y + other.y)
    operator fun minus(other: DecimalPosition): DecimalPosition = DecimalPosition(this.x - other.x, this.y - other.y)
    operator fun minus(other: DecimalOffset): DecimalPosition = DecimalPosition(this.x - other.x, this.y - other.y)
}
typealias PositionLike = Position<*>
