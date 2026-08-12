package com.aston728.engine.core.geometry

public sealed interface Position<T : Number> {
    public val x: T
    public val y: T
    public fun getXDouble(): Double = this.x.toDouble()
    public fun getYDouble(): Double = this.y.toDouble()
}
public data class IntPosition(override val x: Int = 0, override val y: Int = 0) : Position<Int> {
    public operator fun plus(other: IntPosition): IntPosition = IntPosition(this.x + other.x, this.y + other.y)
    public operator fun plus(other: IntOffset): IntPosition = IntPosition(this.x + other.x, this.y + other.y)
    public operator fun minus(other: IntPosition): IntPosition = IntPosition(this.x - other.x, this.y - other.y)
    public operator fun minus(other: IntOffset): IntPosition = IntPosition(this.x - other.x, this.y - other.y)
}
public data class DecimalPosition(override val x: Double = 0.0, override val y: Double = 0.0) : Position<Double> {
    public operator fun plus(other: DecimalPosition): DecimalPosition = DecimalPosition(this.x + other.x, this.y + other.y)
    public operator fun plus(other: DecimalOffset): DecimalPosition = DecimalPosition(this.x + other.x, this.y + other.y)
    public operator fun minus(other: DecimalPosition): DecimalPosition = DecimalPosition(this.x - other.x, this.y - other.y)
    public operator fun minus(other: DecimalOffset): DecimalPosition = DecimalPosition(this.x - other.x, this.y - other.y)
}
public typealias PositionLike = Position<*>
