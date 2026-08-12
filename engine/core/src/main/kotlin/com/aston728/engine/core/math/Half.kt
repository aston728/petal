package com.aston728.engine.core.math

// https://github.com/x448/float16/blob/master/float16.go
private fun floatToHalf(float: Float): UShort {
    val u32: UInt = float.toRawBits().toUInt()
    val sign: UInt = u32 and 0x80000000u
    val exponent: UInt = u32 and 0x7f800000u
    val coefficient: UInt = u32 and 0x007fffffu

    if (exponent == 0x7f800000u) {
        // NaN or Infinity
        var payload: UInt = 0u
        if (coefficient != 0u) {
            payload = coefficient shr 13
            if (payload == 0u) {
                payload = 1u
            }
            payload = payload or 0x0200u
        }
        return ((sign shr 16) or 0x7c00u or payload).toUShort()
    }

    val halfSign: UInt = sign shr 16

    val unbiasedExponent: Int = (exponent shr 23).toInt() - 127
    val halfExponent: Int = unbiasedExponent + 15

    if (halfExponent >= 0x1f) {
        return (halfSign or 0x7c00u).toUShort()
    }

    if (halfExponent <= 0) {
        if (14 - halfExponent > 24) {
            return halfSign.toUShort()
        }
        val fullCoefficient: UInt = coefficient or 0x00800000u
        var halfCoefficient: UInt = fullCoefficient shr (14 - halfExponent)
        val roundBit: UInt = 1u shl (13 - halfExponent)
        if ((fullCoefficient and roundBit) != 0u && (fullCoefficient and (3u * roundBit - 1u)) != 0u) {
            halfCoefficient++
        }
        return (halfSign or halfCoefficient).toUShort()
    }

    val uHalfExponent: UInt = halfExponent.toUInt() shl 10
    val halfCoefficient: UInt = coefficient shr 13
    val roundBit: UInt = 0x00001000u
    if ((coefficient and roundBit) != 0u && (coefficient and (3u * roundBit - 1u)) != 0u) {
        return ((halfSign or uHalfExponent or halfCoefficient) + 1u).toUShort()
    }
    return (halfSign or uHalfExponent or halfCoefficient).toUShort()
}

@JvmInline
public value class Half private constructor(public val bits: UShort) {
    public companion object {
        public val NaN: Half = Half(0x7e00u)
        public val POSITIVE_INFINITY: Half = Half(0x7c00u)
        public val NEGATIVE_INFINITY: Half = Half(0xfc00u)

        public val MAX_VALUE: Half = Half(0x7bffu)
        public val MIN_VALUE: Half = Half(0x0001u)

        public val ZERO: Half = Half(0x0000u)
        public val ONE: Half = Half(0x3c00u)

        public const val SIZE_BITS: Int = 16
        public const val SIZE_BYTES: Int = 2
    }

    public constructor(float: Float) : this(floatToHalf(float))
}
