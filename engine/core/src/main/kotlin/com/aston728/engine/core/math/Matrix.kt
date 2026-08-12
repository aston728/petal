package com.aston728.engine.core.math

public data class Mat2(public val col1: Vec2 = Vec2(), public val col2: Vec2 = Vec2()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray()
}
public data class Mat2x3(public val col1: Vec3 = Vec3(), public val col2: Vec3 = Vec3()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray()
}
public data class Mat2x4(public val col1: Vec4 = Vec4(), public val col2: Vec4 = Vec4()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray()
}
public data class Mat3x2(public val col1: Vec2 = Vec2(), public val col2: Vec2 = Vec2(), public val col3: Vec2 = Vec2()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray() + this.col3.toArray()
}
public data class Mat3(public val col1: Vec3 = Vec3(), public val col2: Vec3 = Vec3(), public val col3: Vec3 = Vec3()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray() + this.col3.toArray()
}
public data class Mat3x4(public val col1: Vec4 = Vec4(), public val col2: Vec4 = Vec4(), public val col3: Vec4 = Vec4()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray() + this.col3.toArray()
}
public data class Mat4x2(public val col1: Vec2 = Vec2(), public val col2: Vec2 = Vec2(), public val col3: Vec2 = Vec2(), public val col4: Vec2 = Vec2()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray() + this.col3.toArray() + this.col4.toArray()
}
public data class Mat4x3(public val col1: Vec3 = Vec3(), public val col2: Vec3 = Vec3(), public val col3: Vec3 = Vec3(), public val col4: Vec3 = Vec3()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray() + this.col3.toArray() + this.col4.toArray()
}
public data class Mat4(public val col1: Vec4 = Vec4(), public val col2: Vec4 = Vec4(), public val col3: Vec4 = Vec4(), public val col4: Vec4 = Vec4()) {
    public fun toFlatArray(): FloatArray = this.col1.toArray() + this.col2.toArray() + this.col3.toArray() + this.col4.toArray()
}

public data class HMat2(public val col1: HVec2 = HVec2(), public val col2: HVec2 = HVec2()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray()
}
public data class HMat2x3(public val col1: HVec3 = HVec3(), public val col2: HVec3 = HVec3()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray()
}
public data class HMat2x4(public val col1: HVec4 = HVec4(), public val col2: HVec4 = HVec4()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray()
}
public data class HMat3x2(public val col1: HVec2 = HVec2(), public val col2: HVec2 = HVec2(), public val col3: HVec2 = HVec2()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray() + this.col3.toArray()
}
public data class HMat3(public val col1: HVec3 = HVec3(), public val col2: HVec3 = HVec3(), public val col3: HVec3 = HVec3()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray() + this.col3.toArray()
}
public data class HMat3x4(public val col1: HVec4 = HVec4(), public val col2: HVec4 = HVec4(), public val col3: HVec4 = HVec4()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray() + this.col3.toArray()
}
public data class HMat4x2(public val col1: HVec2 = HVec2(), public val col2: HVec2 = HVec2(), public val col3: HVec2 = HVec2(), public val col4: HVec2 = HVec2()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray() + this.col3.toArray() + this.col4.toArray()
}
public data class HMat4x3(public val col1: HVec3 = HVec3(), public val col2: HVec3 = HVec3(), public val col3: HVec3 = HVec3(), public val col4: HVec3 = HVec3()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray() + this.col3.toArray() + this.col4.toArray()
}
public data class HMat4(public val col1: HVec4 = HVec4(), public val col2: HVec4 = HVec4(), public val col3: HVec4 = HVec4(), public val col4: HVec4 = HVec4()) {
    public fun toFlatArray(): Array<Half> = this.col1.toArray() + this.col2.toArray() + this.col3.toArray() + this.col4.toArray()
}
