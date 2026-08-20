package com.aston728.engine.core.utils

public data class Color(public val r: UByte, public val g: UByte, public val b: UByte, public val a: UByte = 255u) {
    public fun toNormalizedFloatArray(): FloatArray = floatArrayOf(
        this.r.toFloat() / 255.0f,
        this.g.toFloat() / 255.0f,
        this.b.toFloat() / 255.0f,
        this.a.toFloat() / 255.0f,
    )

    public companion object {
        public val BLACK: Color = Color(0u, 0u, 0u)
        public val WHITE: Color = Color(255u, 255u, 255u)
        public val RED: Color = Color(255u, 0u, 0u)
        public val GREEN: Color = Color(0u, 255u, 0u)
        public val BLUE: Color = Color(0u, 0u, 255u)
        public val YELLOW: Color = Color(255u, 255u, 0u)
        public val MAGENTA: Color = Color(255u, 0u, 255u)
        public val CYAN: Color = Color(0u, 255u, 255u)
        public val PURPLE: Color = Color(128u, 0u, 128u)
    }
}
