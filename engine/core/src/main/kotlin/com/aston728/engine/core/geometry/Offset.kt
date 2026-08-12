package com.aston728.engine.core.geometry

public sealed interface Offset<T : Number> {
    public val x: T
    public val y: T
    public fun getXDouble(): Double = this.x.toDouble()
    public fun getYDouble(): Double = this.y.toDouble()
}
public data class IntOffset(override val x: Int = 0, override val y: Int = 0) : Offset<Int> {
    public operator fun plus(other: IntOffset): IntOffset = IntOffset(this.x + other.x, this.y + other.y)
    public operator fun minus(other: IntOffset): IntOffset = IntOffset(this.x - other.x, this.y - other.y)
}
public data class DecimalOffset(override val x: Double = 0.0, override val y: Double = 0.0) : Offset<Double> {
    public operator fun plus(other: DecimalOffset): DecimalOffset = DecimalOffset(this.x + other.x, this.y + other.y)
    public operator fun minus(other: DecimalOffset): DecimalOffset = DecimalOffset(this.x - other.x, this.y - other.y)
}
public typealias OffsetLike = Offset<*>
