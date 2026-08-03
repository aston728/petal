package com.aston728.engine.elements

import kotlin.math.roundToInt

import com.aston728.engine.utils.Handle

import com.aston728.engine.renderer.ShaderData
import com.aston728.engine.renderer.ShaderSpec

import com.aston728.engine.internals.renderer.GLSLAttributeType

import com.aston728.engine.internals.devices.MouseButton
import com.aston728.engine.internals.devices.Cursor

import com.aston728.engine.internals.utils.ImageUtils
import com.aston728.engine.renderer.ShaderVertexAttributeHandle

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.SizePercentage
import com.aston728.engine.types.Img
import com.aston728.engine.types.Imgs
import com.aston728.engine.types.DrawSequence
import com.aston728.engine.types.GenericUIElement
import com.aston728.engine.types.Handler
import com.aston728.engine.types.Vec2

class Button : UIElement<Button>("Unnamed Button") {
    companion object {
        val DEFAULT_IMG_NORMAL: Img = 0
        val DEFAULT_IMG_HOVERED: Img = 0
        val DEFAULT_IMG_DISABLED: Img = 0

        private val SHADER_POSITION: ShaderVertexAttributeHandle<Vec2> = ShaderVertexAttributeHandle.vec2()
        private val SHADER_SPEC: ShaderSpec = ShaderSpec()
            .setName("Button Shader")
            .addVertexAttribute("iPosition", this.SHADER_POSITION)
            .addOutputAttribute("oColor", GLSLAttributeType.Vec4)
            .setVertexShaderSource("gl_Position = vec4(iPosition, 0.0, 1.0);")
            .setFragmentShaderSource("oColor = vec4(1.0, 1.0, 1.0, 1.0);")
    }

    private var sizePercentage: SizePercentage = SizePercentage(10.0, 5.0)

    private var initImgs: Imgs = listOf(
        Button.DEFAULT_IMG_NORMAL,
        Button.DEFAULT_IMG_HOVERED,
        Button.DEFAULT_IMG_DISABLED,
    )
    private var imgs: Imgs = ImageUtils.scale(this.initImgs, IntSize(0, 0))

    private var isActive: Boolean = true
    private val onClickHandlers: MutableList<Handler> = mutableListOf()

    fun getSizePercentage(): SizePercentage = this.sizePercentage
    fun isActive(): Boolean = this.isActive

    override fun setShader(vararg specs: ShaderSpec, data: List<ShaderData>): Button = apply {
        super.setShader(Button.SHADER_SPEC, *specs, data = data)
    }
    fun setSizePercentage(percentage: SizePercentage): Button = apply {
        this.sizePercentage = percentage
        this.addDirtyFlag(DirtyFlags.SIZE)
    }
    fun setImgs(imgs: Imgs): Button = apply {
        if (imgs.size != 3) {
            this._context.logger.warn("BUTTON", "$this expected 3 images (unhovered, hovered, disabled), got ${imgs.size}")
        } else {
            this.initImgs = imgs
            this.imgs = ImageUtils.scale(imgs, this._rect.getSize())
        }
    }
    fun setActive(isActive: Boolean): Button = apply {
        this.isActive = isActive
    }
    fun toggleActiveness(): Button = this.setActive(!this.isActive)
    fun addOnClickHandler(handler: Handler, handle: Handle? = null): Button = apply {
        this.onClickHandlers.add(handler)
        handle?.setOnRemoveHandler { this.onClickHandlers.remove(handler) }
    }

    override fun commitShaderChange(contextHandle: Long) {
        super.commitShaderChange(contextHandle)
        this._shaderInstanceHandle
            .setVertexAttribute(Button.SHADER_POSITION, arrayOf(Vec2(-1.0f, -1.0f), Vec2(-0.5f, -1.0f), Vec2(-1.0f, -0.5f), Vec2(-0.5f, -0.5f)))
    }

    override fun isHovered(): Boolean = this._rect.isCollidingWith(this._context.mouse.getPosition())
    override fun getCursorType(): Cursor =
        if (this.isActive) Cursor.HAND
        else Cursor.ARROW
    override fun resize(fullSize: IntSize): Unit {
        this._rect.setSize(IntSize(
            (fullSize.width / 100 * this.sizePercentage.width).roundToInt(),
            (fullSize.height / 100 * this.sizePercentage.height).roundToInt(),
        ))
        this.imgs = ImageUtils.scale(this.initImgs, this._rect.getSize())
        return super.resize(fullSize)
    }
    override fun getDrawSequence(hoveredElement: GenericUIElement?): DrawSequence {
        val imgI: Int = when {
            !this.isActive -> 2
            hoveredElement == this -> 1
            else -> 0
        }
        return listOf(this.imgs[imgI] to this._rect.getTopLeft())
    }

    fun click(): Unit {
        if (this.isActive) {
            this.onClickHandlers.toList().forEach { it() }
        }
    }
    override fun update(hoveredElement: GenericUIElement?): Unit {
        if (this._context.mouse.isJustReleased(MouseButton.LEFT) && hoveredElement == this) { this.click() }
        return super.update(hoveredElement)
    }
}
