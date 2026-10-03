package com.aston728.engine.core.elementBase

@JvmInline
internal value class DirtyFlags private constructor(private val bits: Int) {
    internal companion object {
        internal val POSITION: DirtyFlags = DirtyFlags(1 shl 0)
        internal val ANCHOR: DirtyFlags = DirtyFlags(1 shl 1)
        internal val SIZE: DirtyFlags = DirtyFlags(1 shl 2)
        internal val RELATIONSHIP: DirtyFlags = DirtyFlags(1 shl 3)
        internal val SHADER: DirtyFlags = DirtyFlags(1 shl 4)
        internal fun none(): DirtyFlags = DirtyFlags(0)
    }

    internal operator fun contains(other: DirtyFlags): Boolean = (this.bits and other.bits) == other.bits
    internal operator fun plus(other: DirtyFlags): DirtyFlags = DirtyFlags(this.bits or other.bits)
    internal operator fun minus(other: DirtyFlags): DirtyFlags = DirtyFlags(this.bits and other.bits.inv())
}
