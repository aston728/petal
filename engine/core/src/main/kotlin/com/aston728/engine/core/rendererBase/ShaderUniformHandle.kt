package com.aston728.engine.core.rendererBase

import com.aston728.engine.core.math.*

public class ShaderUniformHandle<T> private constructor(public val name: String, public val type: ShaderUniformType) {
    public companion object {
        public fun int(name: String): ShaderUniformHandle<Int> = ShaderUniformHandle(name, ShaderUniformType.Int)
        public fun iVec2(name: String): ShaderUniformHandle<IVec2> = ShaderUniformHandle(name, ShaderUniformType.IVec2)
        public fun iVec3(name: String): ShaderUniformHandle<IVec3> = ShaderUniformHandle(name, ShaderUniformType.IVec3)
        public fun iVec4(name: String): ShaderUniformHandle<IVec4> = ShaderUniformHandle(name, ShaderUniformType.IVec4)
        public fun bool(name: String): ShaderUniformHandle<Boolean> = ShaderUniformHandle(name, ShaderUniformType.Bool)
        public fun boolVec2(name: String): ShaderUniformHandle<BoolVec2> = ShaderUniformHandle(name, ShaderUniformType.BoolVec2)
        public fun boolVec3(name: String): ShaderUniformHandle<BoolVec3> = ShaderUniformHandle(name, ShaderUniformType.BoolVec3)
        public fun boolVec4(name: String): ShaderUniformHandle<BoolVec4> = ShaderUniformHandle(name, ShaderUniformType.BoolVec4)
        public fun uint(name: String): ShaderUniformHandle<UInt> = ShaderUniformHandle(name, ShaderUniformType.UInt)
        public fun uiVec2(name: String): ShaderUniformHandle<UIVec2> = ShaderUniformHandle(name, ShaderUniformType.UIVec2)
        public fun uiVec3(name: String): ShaderUniformHandle<UIVec3> = ShaderUniformHandle(name, ShaderUniformType.UIVec3)
        public fun uiVec4(name: String): ShaderUniformHandle<UIVec4> = ShaderUniformHandle(name, ShaderUniformType.UIVec4)
        public fun float(name: String): ShaderUniformHandle<Float> = ShaderUniformHandle(name, ShaderUniformType.Float)
        public fun vec2(name: String): ShaderUniformHandle<Vec2> = ShaderUniformHandle(name, ShaderUniformType.Vec2)
        public fun vec3(name: String): ShaderUniformHandle<Vec3> = ShaderUniformHandle(name, ShaderUniformType.Vec3)
        public fun vec4(name: String): ShaderUniformHandle<Vec4> = ShaderUniformHandle(name, ShaderUniformType.Vec4)

        public fun mat2(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat2> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat2 } else { ShaderUniformType.Mat2 }
        )
        public fun mat2x3(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat2x3> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat2x3 } else { ShaderUniformType.Mat2x3 }
        )
        public fun mat2x4(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat2x4> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat2x4 } else { ShaderUniformType.Mat2x4 }
        )
        public fun mat3x2(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat3x2> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat3x2 } else { ShaderUniformType.Mat3x2 }
        )
        public fun mat3(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat3> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat3 } else { ShaderUniformType.Mat3 }
        )
        public fun mat3x4(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat3x4> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat3x4 } else { ShaderUniformType.Mat3x4 }
        )
        public fun mat4x2(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat4x2> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat4x2 } else { ShaderUniformType.Mat4x2 }
        )
        public fun mat4x3(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat4x3> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat4x3 } else { ShaderUniformType.Mat4x3 }
        )
        public fun mat4(name: String, shouldTranspose: Boolean = false): ShaderUniformHandle<Mat4> = ShaderUniformHandle(name,
            if (shouldTranspose) { ShaderUniformType.TMat4 } else { ShaderUniformType.Mat4 }
        )

        public fun img2D(name: String): ShaderUniformHandle<ShaderSampledImage2D> = ShaderUniformHandle(name, ShaderUniformType.Image2DSampler)
        public fun img2DArray(name: String): ShaderUniformHandle<ShaderSampledImage2DArray> = ShaderUniformHandle(name, ShaderUniformType.Image2DArraySampler)
    }

    override fun toString(): String = this.name
}
