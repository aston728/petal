package com.aston728.engine.elements

@JvmInline
value class DirtyFlags private constructor(private val bits: Int) {
    companion object {
        val ANCHOR: DirtyFlags = DirtyFlags(1 shl 0)
        val POSITION: DirtyFlags = DirtyFlags(1 shl 1)
        val SIZE: DirtyFlags = DirtyFlags(1 shl 2)
        val RELATIONSHIP: DirtyFlags = DirtyFlags(1 shl 3)
        fun empty(): DirtyFlags = DirtyFlags(0)
    }

    operator fun contains(other: DirtyFlags): Boolean = (this.bits and other.bits) == other.bits
    operator fun plus(other: DirtyFlags): DirtyFlags = DirtyFlags(this.bits or other.bits)
    operator fun minus(other: DirtyFlags): DirtyFlags = DirtyFlags(this.bits and other.bits.inv())

    fun toBinaryString(): String = Integer.toBinaryString(this.bits)
}
