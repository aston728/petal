package com.aston728.engine.internals.renderer

import com.aston728.engine.renderer.Renderer

import com.aston728.engine.utils.Color

import com.aston728.engine.types.DrawSequence

internal class VulkanRenderer : Renderer {
    override fun clearWith(color: Color): Unit {}
    override fun draw(sequence: DrawSequence): Unit {}
}
