package com.aston728.engine.types

data class IVec2(val first: Int = 0, val second: Int = 0)
data class IVec3(val first: Int = 0, val second: Int = 0, val third: Int = 0)
data class IVec4(val first: Int = 0, val second: Int = 0, val third: Int = 0, val fourth: Int = 0)
data class Vec2(val first: Float = 0.0f, val second: Float = 0.0f) {
    fun toArray(): FloatArray = floatArrayOf(this.first, this.second)
}
data class Vec3(val first: Float = 0.0f, val second: Float = 0.0f, val third: Float = 0.0f) {
    fun toArray(): FloatArray = floatArrayOf(this.first, this.second, this.third)
}
data class Vec4(val first: Float = 0.0f, val second: Float = 0.0f, val third: Float = 0.0f, val fourth: Float = 0.0f) {
    fun toArray(): FloatArray = floatArrayOf(this.first, this.second, this.third, this.fourth)
}

data class Mat2(val row1: Vec2 = Vec2(), val row2: Vec2 = Vec2()) {
    fun toFlatArray(): FloatArray = this.row1.toArray() + this.row2.toArray()
}
data class Mat3(val row1: Vec3 = Vec3(), val row2: Vec3 = Vec3(), val row3: Vec3 = Vec3()) {
    fun toFlatArray(): FloatArray = this.row1.toArray() + this.row2.toArray() + this.row3.toArray()
}
data class Mat4(val row1: Vec4 = Vec4(), val row2: Vec4 = Vec4(), val row3: Vec4 = Vec4(), val row4: Vec4 = Vec4()) {
    fun toFlatArray(): FloatArray = this.row1.toArray() + this.row2.toArray() + this.row3.toArray() + this.row4.toArray()
}
