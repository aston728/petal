package com.aston728.engine.utils

import com.aston728.engine.types.Vec3
import com.aston728.engine.types.Vec4

data class Color(val r: Int, val g: Int, val b: Int, val a: Int = 255) {
    init {
        require(this.r in 0..255 && this.g in 0..255 && this.b in 0..255 && this.a in 0..255) {
            "RGBA values must be between 0 and 255, got (${this.r}, ${this.b}, ${this.g}, ${this.a})"
        }
    }

    fun toVec3(): Vec3 = Vec3(this.r / 255.0f, this.g / 255.0f, this.b / 255.0f)
    fun toVec4(): Vec4 = Vec4(this.r / 255.0f, this.g / 255.0f, this.b / 255.0f, this.a / 255.0f)

    companion object {
        val BLACK: Color = Color(0, 0, 0)
        val WHITE: Color = Color(255, 255, 255)
        val RED: Color = Color(255, 0, 0)
        val GREEN: Color = Color(0, 255, 0)
        val BLUE: Color = Color(0, 0, 255)
        val YELLOW: Color = Color(255, 255, 0)
        val MAGENTA: Color = Color(255, 0, 255)
        val CYAN: Color = Color(0, 255, 255)
        val PURPLE: Color = Color(128, 0, 128)
    }
}
