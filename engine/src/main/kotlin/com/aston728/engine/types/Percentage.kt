package com.aston728.engine.types

data class SizePercentage(val width: Double, val height: Double) {
    init {
        require(this.width >= 0 && this.height >= 0) { "width and height percentages must be positive, got (${this.width}%, ${this.height}%)" }
    }

    operator fun plus(other: SizePercentage): SizePercentage = SizePercentage(this.width + other.width, this.height + other.height)
    operator fun minus(other: SizePercentage): SizePercentage = SizePercentage(this.width - other.width, this.height - other.height)
}
