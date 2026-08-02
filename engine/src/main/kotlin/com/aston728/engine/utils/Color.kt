package com.aston728.engine.utils

data class Color(val r: Int, val g: Int, val b: Int, val a: Int = 255) {
    init {
        require(this.r in 0..255 && this.g in 0..255 && this.b in 0..255 && this.a in 0..255) {
            "RGBA values must be between 0 and 255, got (${this.r}, ${this.b}, ${this.g}, ${this.a})"
        }
    }

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
