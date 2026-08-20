package com.aston728.engine.core.math

public data class IVec2(public val first: Int = 0, public val second: Int = 0)
public data class IVec3(public val first: Int = 0, public val second: Int = 0, public val third: Int = 0)
public data class IVec4(public val first: Int = 0, public val second: Int = 0, public val third: Int = 0, public val fourth: Int = 0)

public data class BoolVec2(public val first: Boolean = false, public val second: Boolean = false)
public data class BoolVec3(public val first: Boolean = false, public val second: Boolean = false, public val third: Boolean = false)
public data class BoolVec4(public val first: Boolean = false, public val second: Boolean = false, public val third: Boolean = false, public val fourth: Boolean = false)

public data class BVec2(public val first: Byte = 0, public val second: Byte = 0)
public data class BVec3(public val first: Byte = 0, public val second: Byte = 0, public val third: Byte = 0)
public data class BVec4(public val first: Byte = 0, public val second: Byte = 0, public val third: Byte = 0, public val fourth: Byte = 0)

public data class SVec2(public val first: Short = 0, public val second: Short = 0)
public data class SVec3(public val first: Short = 0, public val second: Short = 0, public val third: Short = 0)
public data class SVec4(public val first: Short = 0, public val second: Short = 0, public val third: Short = 0, public val fourth: Short = 0)

public data class UIVec2(public val first: UInt = 0u, public val second: UInt = 0u)
public data class UIVec3(public val first: UInt = 0u, public val second: UInt = 0u, public val third: UInt = 0u)
public data class UIVec4(public val first: UInt = 0u, public val second: UInt = 0u, public val third: UInt = 0u, public val fourth: UInt = 0u)

public data class UBVec2(public val first: UByte = 0u, public val second: UByte = 0u)
public data class UBVec3(public val first: UByte = 0u, public val second: UByte = 0u, public val third: UByte = 0u)
public data class UBVec4(public val first: UByte = 0u, public val second: UByte = 0u, public val third: UByte = 0u, public val fourth: UByte = 0u)

public data class USVec2(public val first: UShort = 0u, public val second: UShort = 0u)
public data class USVec3(public val first: UShort = 0u, public val second: UShort = 0u, public val third: UShort = 0u)
public data class USVec4(public val first: UShort = 0u, public val second: UShort = 0u, public val third: UShort = 0u, public val fourth: UShort = 0u)

public data class Vec2(public val first: Float = 0.0f, public val second: Float = 0.0f) {
    public fun toArray(): FloatArray = floatArrayOf(this.first, this.second)
}
public data class Vec3(public val first: Float = 0.0f, public val second: Float = 0.0f, public val third: Float = 0.0f) {
    public fun toArray(): FloatArray = floatArrayOf(this.first, this.second, this.third)
}
public data class Vec4(public val first: Float = 0.0f, public val second: Float = 0.0f, public val third: Float = 0.0f, public val fourth: Float = 0.0f) {
    public fun toArray(): FloatArray = floatArrayOf(this.first, this.second, this.third, this.fourth)
}
public data class HVec2(public val first: Half = Half.ZERO, public val second: Half = Half.ZERO) {
    public fun toArray(): Array<Half> = arrayOf(this.first, this.second)
}
public data class HVec3(public val first: Half = Half.ZERO, public val second: Half = Half.ZERO, public val third: Half = Half.ZERO) {
    public fun toArray(): Array<Half> = arrayOf(this.first, this.second, this.third)
}
public data class HVec4(public val first: Half = Half.ZERO, public val second: Half = Half.ZERO, public val third: Half = Half.ZERO, public val fourth: Half = Half.ZERO) {
    public fun toArray(): Array<Half> = arrayOf(this.first, this.second, this.third, this.fourth)
}
