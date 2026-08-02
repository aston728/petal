package com.aston728.engine.renderer

import com.aston728.engine.internals.renderer.GLSLType
import org.lwjgl.opengl.GL30C.*

import com.aston728.engine.types.*

internal class ShaderUniformDescriptor<T>(internal val type: GLSLType, internal val updater: (Int, T) -> Unit)
private object ShaderUniformDescriptors {
    // TODO: more types
    val INT: ShaderUniformDescriptor<Int> = ShaderUniformDescriptor(GLSLType.Int) { location, value ->
        glUniform1i(location, value)
    }
    val IVEC2: ShaderUniformDescriptor<IVec2> = ShaderUniformDescriptor(GLSLType.IVec2) { location, value ->
        glUniform2i(location, value.first, value.second)
    }
    val IVEC3: ShaderUniformDescriptor<IVec3> = ShaderUniformDescriptor(GLSLType.IVec3) { location, value ->
        glUniform3i(location, value.first, value.second, value.third)
    }
    val IVEC4: ShaderUniformDescriptor<IVec4> = ShaderUniformDescriptor(GLSLType.IVec4) { location, value ->
        glUniform4i(location, value.first, value.second, value.third, value.fourth)
    }
    val UINT: ShaderUniformDescriptor<Int> = ShaderUniformDescriptor(GLSLType.UInt) { location, value ->
        glUniform1ui(location, value)
    }
    val UIVEC2: ShaderUniformDescriptor<IVec2> = ShaderUniformDescriptor(GLSLType.UIVec2) { location, value ->
        glUniform2ui(location, value.first, value.second)
    }
    val UIVEC3: ShaderUniformDescriptor<IVec3> = ShaderUniformDescriptor(GLSLType.UIVec3) { location, value ->
        glUniform3ui(location, value.first, value.second, value.third)
    }
    val UIVEC4: ShaderUniformDescriptor<IVec4> = ShaderUniformDescriptor(GLSLType.UIVec4) { location, value ->
        glUniform4ui(location, value.first, value.second, value.third, value.fourth)
    }
    val FLOAT: ShaderUniformDescriptor<Float> = ShaderUniformDescriptor(GLSLType.Float) { location, value ->
        glUniform1f(location, value)
    }
    val VEC2: ShaderUniformDescriptor<Vec2> = ShaderUniformDescriptor(GLSLType.Vec2) { location, value ->
        glUniform2f(location, value.first, value.second)
    }
    val VEC3: ShaderUniformDescriptor<Vec3> = ShaderUniformDescriptor(GLSLType.Vec3) { location, value ->
        glUniform3f(location, value.first, value.second, value.third)
    }
    val VEC4: ShaderUniformDescriptor<Vec4> = ShaderUniformDescriptor(GLSLType.Vec4) { location, value ->
        glUniform4f(location, value.first, value.second, value.third, value.fourth)
    }

    val MAT2: ShaderUniformDescriptor<Mat2> = ShaderUniformDescriptor(GLSLType.Mat2) { location, value ->
        glUniformMatrix2fv(location, false, value.toFlatArray())
    }
    val TMAT2: ShaderUniformDescriptor<Mat2> = ShaderUniformDescriptor(GLSLType.Mat2) { location, value ->
        glUniformMatrix2fv(location, true, value.toFlatArray())
    }
    val MAT3: ShaderUniformDescriptor<Mat3> = ShaderUniformDescriptor(GLSLType.Mat3) { location, value ->
        glUniformMatrix3fv(location, false, value.toFlatArray())
    }
    val TMAT3: ShaderUniformDescriptor<Mat3> = ShaderUniformDescriptor(GLSLType.Mat3) { location, value ->
        glUniformMatrix3fv(location, true, value.toFlatArray())
    }
    val MAT4: ShaderUniformDescriptor<Mat4> = ShaderUniformDescriptor(GLSLType.Mat4) { location, value ->
        glUniformMatrix4fv(location, false, value.toFlatArray())
    }
    val TMAT4: ShaderUniformDescriptor<Mat4> = ShaderUniformDescriptor(GLSLType.Mat4) { location, value ->
        glUniformMatrix4fv(location, true, value.toFlatArray())
    }
}

class ShaderUniformHandle<T> private constructor(internal val descriptor: ShaderUniformDescriptor<T>) {
    companion object {
        fun int(): ShaderUniformHandle<Int> = ShaderUniformHandle(ShaderUniformDescriptors.INT)
        fun iVec2(): ShaderUniformHandle<IVec2> = ShaderUniformHandle(ShaderUniformDescriptors.IVEC2)
        fun iVec3(): ShaderUniformHandle<IVec3> = ShaderUniformHandle(ShaderUniformDescriptors.IVEC3)
        fun iVec4(): ShaderUniformHandle<IVec4> = ShaderUniformHandle(ShaderUniformDescriptors.IVEC4)
        fun uint(): ShaderUniformHandle<Int> = ShaderUniformHandle(ShaderUniformDescriptors.UINT)
        fun uiVec2(): ShaderUniformHandle<IVec2> = ShaderUniformHandle(ShaderUniformDescriptors.UIVEC2)
        fun uiVec3(): ShaderUniformHandle<IVec3> = ShaderUniformHandle(ShaderUniformDescriptors.UIVEC3)
        fun uiVec4(): ShaderUniformHandle<IVec4> = ShaderUniformHandle(ShaderUniformDescriptors.UIVEC4)
        fun float(): ShaderUniformHandle<Float> = ShaderUniformHandle(ShaderUniformDescriptors.FLOAT)
        fun vec2(): ShaderUniformHandle<Vec2> = ShaderUniformHandle(ShaderUniformDescriptors.VEC2)
        fun vec3(): ShaderUniformHandle<Vec3> = ShaderUniformHandle(ShaderUniformDescriptors.VEC3)
        fun vec4(): ShaderUniformHandle<Vec4> = ShaderUniformHandle(ShaderUniformDescriptors.VEC4)

        fun mat2(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat2> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformDescriptors.TMAT2 } else { ShaderUniformDescriptors.MAT2 }
        )
        fun mat3(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat3> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformDescriptors.TMAT3 } else { ShaderUniformDescriptors.MAT3 }
        )
        fun mat4(shouldTranspose: Boolean = false): ShaderUniformHandle<Mat4> = ShaderUniformHandle(
            if (shouldTranspose) { ShaderUniformDescriptors.TMAT4 } else { ShaderUniformDescriptors.MAT4 }
        )
    }
}
