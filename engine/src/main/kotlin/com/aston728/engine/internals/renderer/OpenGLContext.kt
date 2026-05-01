package com.aston728.engine.internals.renderer

import org.lwjgl.opengl.*
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL43C.*
import org.lwjgl.system.MemoryUtil.NULL

import com.aston728.engine.renderer.GraphicsContext

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.ErrorHandler

internal class OpenGLContext(private val handle: Long, isDebugOn: Boolean, debugMessageCallback: ErrorHandler) : GraphicsContext() {
    private val capabilities: GLCapabilities = this.createCapabilities()
    init {
        if (isDebugOn) { this.enableDebugging(debugMessageCallback) }
    }

    private fun createCapabilities(): GLCapabilities {
        glfwMakeContextCurrent(this.handle)
        return GL.createCapabilities()
    }

    private fun formatDebugMessageNormal(source: Int, type: Int, severity: Int, messageLength: Int, message: Long): String {
        val sourceString: String = when (source) {
            GL_DEBUG_SOURCE_API -> "API"
            GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW SYSTEM"
            GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER COMPILER"
            GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD PARTY"
            GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION"
            GL_DEBUG_SOURCE_OTHER -> "OTHER"
            else -> "UNKNOWN"
        }
        val typeString: String = when (type) {
            GL_DEBUG_TYPE_ERROR -> "ERROR"
            GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED BEHAVIOR"
            GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED BEHAVIOR"
            GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY"
            GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE"
            GL_DEBUG_TYPE_MARKER -> "MARKER"
            GL_DEBUG_TYPE_PUSH_GROUP -> "PUSH GROUP"
            GL_DEBUG_TYPE_POP_GROUP -> "POP GROUP"
            GL_DEBUG_TYPE_OTHER -> "OTHER"
            else -> "UNKNOWN"
        }
        val severityString: String = when (severity) {
            GL_DEBUG_SEVERITY_HIGH -> "HIGH"
            GL_DEBUG_SEVERITY_MEDIUM -> "MEDIUM"
            GL_DEBUG_SEVERITY_LOW -> "LOW"
            GL_DEBUG_SEVERITY_NOTIFICATION -> "NOTIFICATION"
            else -> "UNKNOWN"
        }
        val description: String = GLDebugMessageCallback.getMessage(messageLength, message)
        return "Source: $sourceString\nType: $typeString\nSeverity: $severityString\nMessage: $description"
    }
    private fun formatDebugMessageARB(source: Int, type: Int, severity: Int, messageLength: Int, message: Long): String {
        val sourceString: String = when (source) {
            ARBDebugOutput.GL_DEBUG_SOURCE_API_ARB -> "API"
            ARBDebugOutput.GL_DEBUG_SOURCE_WINDOW_SYSTEM_ARB -> "WINDOW SYSTEM"
            ARBDebugOutput.GL_DEBUG_SOURCE_SHADER_COMPILER_ARB -> "SHADER COMPILER"
            ARBDebugOutput.GL_DEBUG_SOURCE_THIRD_PARTY_ARB -> "THIRD PARTY"
            ARBDebugOutput.GL_DEBUG_SOURCE_APPLICATION_ARB -> "APPLICATION"
            ARBDebugOutput.GL_DEBUG_SOURCE_OTHER_ARB -> "OTHER"
            else -> "UNKNOWN"
        }
        val typeString: String = when (type) {
            ARBDebugOutput.GL_DEBUG_TYPE_ERROR_ARB -> "ERROR"
            ARBDebugOutput.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR_ARB -> "DEPRECATED BEHAVIOR"
            ARBDebugOutput.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR_ARB -> "UNDEFINED BEHAVIOR"
            ARBDebugOutput.GL_DEBUG_TYPE_PORTABILITY_ARB -> "PORTABILITY"
            ARBDebugOutput.GL_DEBUG_TYPE_PERFORMANCE_ARB -> "PERFORMANCE"
            ARBDebugOutput.GL_DEBUG_TYPE_OTHER_ARB -> "OTHER"
            else -> "UNKNOWN"
        }
        val severityString: String = when (severity) {
            ARBDebugOutput.GL_DEBUG_SEVERITY_HIGH_ARB -> "HIGH"
            ARBDebugOutput.GL_DEBUG_SEVERITY_MEDIUM_ARB -> "MEDIUM"
            ARBDebugOutput.GL_DEBUG_SEVERITY_LOW_ARB -> "LOW"
            else -> "UNKNOWN"
        }
        val description: String = GLDebugMessageARBCallback.getMessage(messageLength, message)
        return "Source: $sourceString\nType: $typeString\nSeverity: $severityString\nMessage: $description"
    }
    private fun formatDebugMessageAMD(category: Int, severity: Int, messageLength: Int, message: Long): String {
        val categoryString: String = when (category) {
            AMDDebugOutput.GL_DEBUG_CATEGORY_API_ERROR_AMD -> "API"
            AMDDebugOutput.GL_DEBUG_CATEGORY_WINDOW_SYSTEM_AMD -> "WINDOW SYSTEM"
            AMDDebugOutput.GL_DEBUG_CATEGORY_SHADER_COMPILER_AMD -> "SHADER COMPILER"
            AMDDebugOutput.GL_DEBUG_CATEGORY_APPLICATION_AMD -> "APPLICATION"
            AMDDebugOutput.GL_DEBUG_CATEGORY_DEPRECATION_AMD -> "DEPRECATED BEHAVIOR"
            AMDDebugOutput.GL_DEBUG_CATEGORY_UNDEFINED_BEHAVIOR_AMD -> "UNDEFINED BEHAVIOR"
            AMDDebugOutput.GL_DEBUG_CATEGORY_PERFORMANCE_AMD -> "PERFORMANCE"
            AMDDebugOutput.GL_DEBUG_CATEGORY_OTHER_AMD -> "OTHER"
            else -> "UNKNOWN"
        }
        val severityString: String = when (severity) {
            AMDDebugOutput.GL_DEBUG_SEVERITY_HIGH_AMD -> "HIGH"
            AMDDebugOutput.GL_DEBUG_SEVERITY_MEDIUM_AMD -> "MEDIUM"
            AMDDebugOutput.GL_DEBUG_SEVERITY_LOW_AMD -> "LOW"
            else -> "UNKNOWN"
        }
        val description: String = GLDebugMessageAMDCallback.getMessage(messageLength, message)
        return "Source: $categoryString\nSeverity: $severityString\nMessage: $description"
    }
    override fun enableDebugging(messageCallback: ErrorHandler): Unit {
        if ((glGetInteger(GL_CONTEXT_FLAGS) and GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
            messageCallback("Failed to set a debug message callback, a debug context is not present")
            return
        }

        glEnable(GL_DEBUG_OUTPUT)
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)

        if (this.capabilities.OpenGL43) {
            glDebugMessageCallback(
                GLDebugMessageCallback.create { source, type, id, severity, messageLength, message, userParameter ->
                    messageCallback(this.formatDebugMessageNormal(source, type, severity, messageLength, message))
                }, NULL
            )
        } else if (this.capabilities.GL_KHR_debug) {
            KHRDebug.glDebugMessageCallback(
                GLDebugMessageCallback.create { source, type, id, severity, messageLength, message, userParameter ->
                    messageCallback(this.formatDebugMessageNormal(source, type, severity, messageLength, message))
                }, NULL
            )
        } else if (this.capabilities.GL_ARB_debug_output) {
            ARBDebugOutput.glDebugMessageCallbackARB(
                GLDebugMessageARBCallback.create { source, type, id, severity, messageLength, message, userParameter ->
                    messageCallback(this.formatDebugMessageARB(source, type, severity, messageLength, message))
                }, NULL
            )
        } else if (this.capabilities.GL_AMD_debug_output) {
            AMDDebugOutput.glDebugMessageCallbackAMD(
                GLDebugMessageAMDCallback.create { id, category, severity, messageLength, message, userParameter ->
                    messageCallback(this.formatDebugMessageAMD(category, severity, messageLength, message))
                }, NULL
            )
        } else {
            messageCallback("Failed to set a debug message callback, debug message callbacks are not supported")
        }
    }

    override fun onResize(size: IntSize): Unit {
        glfwMakeContextCurrent(this.handle)
        GL.setCapabilities(this.capabilities)
        glViewport(0, 0, size.width, size.height)
    }

    override fun startDrawing(): Unit {
        glfwMakeContextCurrent(this.handle)
        GL.setCapabilities(this.capabilities)
    }
    override fun stopDrawing(): Unit {
        glfwSwapBuffers(this.handle)
    }
}
