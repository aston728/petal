package com.aston728.engine.internals.renderer

import org.lwjgl.opengl.GL11C.*

import com.aston728.engine.renderer.Renderer

import com.aston728.engine.utils.Color

import com.aston728.engine.types.DrawSequence

internal class OpenGLRenderer : Renderer {
    override fun clearWith(color: Color): Unit {
        glClearColor(color.r / 255.0f, color.g / 255.0f, color.b / 255.0f, color.a / 255.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }
    override fun draw(sequence: DrawSequence): Unit {
    }
}
