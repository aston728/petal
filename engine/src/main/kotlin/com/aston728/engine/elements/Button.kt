package com.aston728.engine.elements

import kotlin.math.roundToInt

import com.aston728.engine.utils.Handle

import com.aston728.engine.internals.devices.MouseButton
import com.aston728.engine.internals.devices.Cursor

import com.aston728.engine.internals.utils.ImageUtils

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.SizePercentage
import com.aston728.engine.types.Img
import com.aston728.engine.types.Imgs
import com.aston728.engine.types.DrawSequence
import com.aston728.engine.types.GenericUIElement
import com.aston728.engine.types.Handler

class Button : UIElement<Button>() {
    companion object {
        val DEFAULT_IMG_NORMAL: Img = 0
        val DEFAULT_IMG_HOVERED: Img = 0
        val DEFAULT_IMG_DISABLED: Img = 0
    }

    init { this._name = "Unnamed Button" }

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
