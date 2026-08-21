package com.aston728.engine.openGLRenderer

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.internals.image.Image
import com.aston728.engine.core.rendererBase.ShaderSampler
import com.aston728.engine.core.rendererBase.ShaderSamplerAddressMode
import com.aston728.engine.core.rendererBase.ShaderSamplerFilter
import com.aston728.engine.core.rendererBase.ShaderSamplerMipmapMode
import org.lwjgl.opengl.GL33C.*
import org.lwjgl.system.MemoryUtil.NULL

internal class OpenGLShaderSamplerManager {
    private val texture2DMappings: MutableMap<Image, Int> = mutableMapOf()
    private val texture2DArrayMappings: MutableMap<List<Image>, Int> = mutableMapOf()
    private val samplerMappings: MutableMap<ShaderSampler, Int> = mutableMapOf()

    internal fun getTexture2D(img: Image): Int = this.texture2DMappings.getOrPut(img) {
        val texture: Int = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, img.getPixels())
        glGenerateMipmap(GL_TEXTURE_2D)
        texture
    }
    internal fun getTexture2DArray(imgs: List<Image>): Int = this.texture2DArrayMappings.getOrPut(imgs) {
        val width: Int = (imgs.maxBy { it.getWidth() }).getWidth()
        val height: Int = (imgs.maxBy { it.getHeight() }).getHeight()
        val imgs: List<Image> = imgs.map { it.scaledTo(IntSize(width, height)) }

        val texture: Int = glGenTextures()
        glBindTexture(GL_TEXTURE_2D_ARRAY, texture)
        glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8, width, height, imgs.size, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL)
        imgs.forEachIndexed { i, img ->
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, img.getPixels())
        }
        glGenerateMipmap(GL_TEXTURE_2D_ARRAY)
        texture
    }

    private fun getGLFilter(filter: ShaderSamplerFilter, mipmapMode: ShaderSamplerMipmapMode): Int = when (filter) {
        ShaderSamplerFilter.NEAREST -> when (mipmapMode) {
            ShaderSamplerMipmapMode.NONE -> GL_NEAREST
            ShaderSamplerMipmapMode.NEAREST -> GL_NEAREST_MIPMAP_NEAREST
            ShaderSamplerMipmapMode.LINEAR -> GL_NEAREST_MIPMAP_LINEAR
        }
        ShaderSamplerFilter.LINEAR -> when (mipmapMode) {
            ShaderSamplerMipmapMode.NONE -> GL_LINEAR
            ShaderSamplerMipmapMode.NEAREST -> GL_LINEAR_MIPMAP_NEAREST
            ShaderSamplerMipmapMode.LINEAR -> GL_LINEAR_MIPMAP_LINEAR
        }
    }
    private fun getGLWrap(wrap: ShaderSamplerAddressMode): Int = when (wrap) {
        ShaderSamplerAddressMode.EDGE_CLAMP -> GL_CLAMP_TO_EDGE
        ShaderSamplerAddressMode.BORDER_CLAMP -> GL_CLAMP_TO_BORDER
        ShaderSamplerAddressMode.REPEAT -> GL_REPEAT
        ShaderSamplerAddressMode.REPEAT_MIRRORED -> GL_MIRRORED_REPEAT
    }
    internal fun getGLSampler(sampler: ShaderSampler): Int = this.samplerMappings.getOrPut(sampler) {
        val glSampler: Int = glGenSamplers()
        glSamplerParameteri(glSampler, GL_TEXTURE_MIN_FILTER, this.getGLFilter(sampler.minFilter, sampler.mipmapMode))
        glSamplerParameteri(glSampler, GL_TEXTURE_MAG_FILTER, this.getGLFilter(sampler.magFilter, sampler.mipmapMode))
        glSamplerParameteri(glSampler, GL_TEXTURE_WRAP_S, this.getGLWrap(sampler.addressModeX))
        glSamplerParameteri(glSampler, GL_TEXTURE_WRAP_T, this.getGLWrap(sampler.addressModeY))
        glSamplerParameterfv(glSampler, GL_TEXTURE_BORDER_COLOR, sampler.borderColor.toNormalizedFloatArray())
        glSamplerParameterf(glSampler, GL_TEXTURE_MIN_LOD, sampler.minLOD)
        glSamplerParameterf(glSampler, GL_TEXTURE_MAX_LOD, sampler.maxLOD)
        glSampler
    }

    internal fun free(): Unit {
        this.texture2DMappings.values.forEach { glDeleteTextures(it) }
        this.texture2DMappings.clear()
        this.texture2DArrayMappings.values.forEach { glDeleteTextures(it) }
        this.texture2DArrayMappings.clear()
        this.samplerMappings.values.forEach { glDeleteSamplers(it) }
        this.samplerMappings.clear()
    }
}
