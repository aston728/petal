package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.math.*

@JvmInline
public value class ShaderUniformHandle<T> private constructor(public val type: ShaderUniformType) {
    public companion object {
        public fun int(): ShaderUniformHandle<Int> = ShaderUniformHandle(ShaderUniformType.Int)
        public fun iVec2(): ShaderUniformHandle<IVec2> = ShaderUniformHandle(ShaderUniformType.IVec2)
        public fun iVec3(): ShaderUniformHandle<IVec3> = ShaderUniformHandle(ShaderUniformType.IVec3)
        public fun iVec4(): ShaderUniformHandle<IVec4> = ShaderUniformHandle(ShaderUniformType.IVec4)
        public fun uint(): ShaderUniformHandle<UInt> = ShaderUniformHandle(ShaderUniformType.UInt)
        public fun uiVec2(): ShaderUniformHandle<UIVec2> = ShaderUniformHandle(ShaderUniformType.UIVec2)
        public fun uiVec3(): ShaderUniformHandle<UIVec3> = ShaderUniformHandle(ShaderUniformType.UIVec3)
        public fun uiVec4(): ShaderUniformHandle<UIVec4> = ShaderUniformHandle(ShaderUniformType.UIVec4)
        public fun float(): ShaderUniformHandle<Float> = ShaderUniformHandle(ShaderUniformType.Float)
        public fun vec2(): ShaderUniformHandle<Vec2> = ShaderUniformHandle(ShaderUniformType.Vec2)
        public fun vec3(): ShaderUniformHandle<Vec3> = ShaderUniformHandle(ShaderUniformType.Vec3)
        public fun vec4(): ShaderUniformHandle<Vec4> = ShaderUniformHandle(ShaderUniformType.Vec4)

        public fun mat2(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat2> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat2 } else { ShaderUniformType.Mat2 }
        )
        public fun mat2x3(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat2x3> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat2x3 } else { ShaderUniformType.Mat2x3 }
        )
        public fun mat2x4(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat2x4> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat2x4 } else { ShaderUniformType.Mat2x4 }
        )
        public fun mat3x2(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat3x2> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat3x2 } else { ShaderUniformType.Mat3x2 }
        )
        public fun mat3(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat3> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat3 } else { ShaderUniformType.Mat3 }
        )
        public fun mat3x4(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat3x4> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat3x4 } else { ShaderUniformType.Mat3x4 }
        )
        public fun mat4x2(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat4x2> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat4x2 } else { ShaderUniformType.Mat4x2 }
        )
        public fun mat4x3(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat4x3> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat4x3 } else { ShaderUniformType.Mat4x3 }
        )
        public fun mat4(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat4> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformType.TMat4 } else { ShaderUniformType.Mat4 }
        )
    }
}
