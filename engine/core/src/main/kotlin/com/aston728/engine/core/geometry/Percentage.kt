package com.aston728.engine.core.geometry

public data class SizePercentage(public val width: Double, public val height: Double) {
    init {
        require(this.width >= 0 && this.height >= 0) { "Width and height percentages must be positive, got (${this.width}%, ${this.height}%)" }
    }

    public operator fun plus(other: SizePercentage): SizePercentage = SizePercentage(this.width + other.width, this.height + other.height)
    public operator fun minus(other: SizePercentage): SizePercentage = SizePercentage(this.width - other.width, this.height - other.height)
}
