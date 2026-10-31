package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.rendererBase.Shader
import org.lwjgl.opengl.GL33C.*

internal class OpenGLShader(
    name: String, errorCallback: (String) -> Unit,
    vertexSource: String, fragmentSource: String,
) : Shader(name, errorCallback) {
    companion object {
        internal const val INVALID_PROGRAM: Int = -1
    }

    internal var program: Int = this.createProgram(vertexSource, fragmentSource)
    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader: Int = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, vertexSource)
        glCompileShader(vertexShader)
        val vertexShaderError: String = glGetShaderInfoLog(vertexShader)
        if (vertexShaderError.isNotEmpty()) { // Exit on warning too
            this._errorCallback("Failed to create vertex shader for $this: $vertexShaderError")

            glDeleteShader(vertexShader)
            return OpenGLShader.INVALID_PROGRAM
        }

        val fragmentShader: Int = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentSource)
        glCompileShader(fragmentShader)
        val fragmentShaderError: String = glGetShaderInfoLog(fragmentShader)
        if (fragmentShaderError.isNotEmpty()) { // Exit on warning too
            this._errorCallback("Failed to create fragment shader for $this: $fragmentShaderError")

            glDeleteShader(vertexShader)
            glDeleteShader(fragmentShader)
            return OpenGLShader.INVALID_PROGRAM
        }

        val program: Int = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
        val programError: String = glGetProgramInfoLog(program)
        if (programError.isNotEmpty()) { // Exit on warning too
            this._errorCallback("Failed to create program for $this: $programError")

            glDeleteShader(vertexShader)
            glDeleteShader(fragmentShader)
            glDeleteProgram(program)
            return OpenGLShader.INVALID_PROGRAM
        }

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
        return program
    }

    override fun onFree(): Unit {
        glDeleteProgram(this.program)
        this.program = OpenGLShader.INVALID_PROGRAM
    }
    override fun exists(): Boolean = this.program != OpenGLShader.INVALID_PROGRAM
    override fun onBind(): Unit {
        glUseProgram(this.program)
    }
}
