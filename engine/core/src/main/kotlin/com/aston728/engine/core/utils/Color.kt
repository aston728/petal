package com.aston728.engine.core.utils

import com.aston728.engine.core.math.Vec3
import com.aston728.engine.core.math.Vec4

public data class Color(public val r: Int, public val g: Int, public val b: Int, public val a: Int = 255) {
    init {
        require(this.r in 0..255 && this.g in 0..255 && this.b in 0..255 && this.a in 0..255) {
            "RGBA values must be between 0 and 255, got (${this.r}, ${this.b}, ${this.g}, ${this.a})"
        }
    }

    public fun toVec3(): Vec3 = Vec3(this.r / 255.0f, this.g / 255.0f, this.b / 255.0f)
    public fun toVec4(): Vec4 = Vec4(this.r / 255.0f, this.g / 255.0f, this.b / 255.0f, this.a / 255.0f)

    public companion object {
        public val BLACK: Color = Color(0, 0, 0)
        public val WHITE: Color = Color(255, 255, 255)
        public val RED: Color = Color(255, 0, 0)
        public val GREEN: Color = Color(0, 255, 0)
        public val BLUE: Color = Color(0, 0, 255)
        public val YELLOW: Color = Color(255, 255, 0)
        public val MAGENTA: Color = Color(255, 0, 255)
        public val CYAN: Color = Color(0, 255, 255)
        public val PURPLE: Color = Color(128, 0, 128)
    }
}
