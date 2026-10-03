package com.aston728.engine.elements

import com.aston728.engine.core.elementBase.UIElement
import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.internals.Image
import com.aston728.engine.core.internals.devices.Cursor
import com.aston728.engine.core.internals.devices.MouseButton
import com.aston728.engine.core.math.Vec2
import com.aston728.engine.core.rendererBase.ShaderAttributeType
import com.aston728.engine.core.rendererBase.ShaderData
import com.aston728.engine.core.rendererBase.ShaderSpec
import com.aston728.engine.core.rendererBase.ShaderVertexAttributeHandle
import com.aston728.engine.core.utils.Handle
import kotlin.math.roundToInt

public class Button : UIElement<Button>("Unnamed Button") {
    public companion object {
        public val DEFAULT_IMG_NORMAL: Image = Image.DEFAULT_IMG
        public val DEFAULT_IMG_HOVERED: Image = Image.DEFAULT_IMG
        public val DEFAULT_IMG_DISABLED: Image = Image.DEFAULT_IMG

        private val SHADER_POSITION: ShaderVertexAttributeHandle<Vec2> = ShaderVertexAttributeHandle.vec2("iPosition")
        private val SHADER_SPEC: ShaderSpec = ShaderSpec()
            .setName("Button Shader")
            .addVertexAttribute(this.SHADER_POSITION)
            .addOutputAttribute("oColor", ShaderAttributeType.Vec4)
            .setVertexShaderBody("gl_Position = vec4(iPosition, 0.0, 1.0);")
            .setFragmentShaderBody("oColor = vec4(1.0, 1.0, 1.0, 1.0);")
    }

    private var initImgs: List<Image> = listOf(
        Button.DEFAULT_IMG_NORMAL,
        Button.DEFAULT_IMG_HOVERED,
        Button.DEFAULT_IMG_DISABLED,
    )
    private var imgs: List<Image> = this.initImgs.map { it.scaledTo(IntSize(0, 0)) }

    private var isActive: Boolean = true
    private val onClickHandlers: MutableList<(Button) -> Unit> = mutableListOf()

    init {
        this.addShaderChangeHandler({ _, newInstance ->
            newInstance
                .setVertexAttribute(Button.SHADER_POSITION, arrayOf(Vec2(-1.0f, -0.5f), Vec2(-0.5f, -0.5f), Vec2(-0.5f,  0.5f), Vec2(-1.0f,  0.5f)))
        })
    }

    public fun isActive(): Boolean = this.isActive

    override fun setShader(vararg specs: ShaderSpec, data: List<ShaderData>): Button = apply {
        super.setShader(Button.SHADER_SPEC, *specs, data = data)
    }
    public fun setImgs(
        normalImg: Image = Button.DEFAULT_IMG_NORMAL,
        hoveredImg: Image = Button.DEFAULT_IMG_HOVERED,
        disabledImg: Image = Button.DEFAULT_IMG_DISABLED,
    ): Button = apply {
        this.initImgs = listOf(normalImg, hoveredImg, disabledImg)
        this.imgs = this.initImgs.map { it.scaledTo(this._rect.getSize()) }
    }
    public fun setActive(isActive: Boolean): Button = apply {
        this.isActive = isActive
    }
    public fun toggleActiveness(): Button = this.setActive(!this.isActive)
    public fun addOnClickHandler(handler: (Button) -> Unit, handle: Handle? = null): Button = apply {
        val wrapper: (Button) -> Unit = { button -> handler(button) }
        this.onClickHandlers.add(wrapper)
        handle?.setOnRemoveHandler { this.onClickHandlers.remove(wrapper) }
    }

    override fun getCursorType(): Cursor = if (this.isActive) { Cursor.HAND } else { Cursor.ARROW }
    override fun onResize(fullSize: IntSize): Unit {
        this._rect.setSize(IntSize(
            (fullSize.width / 100 * this._sizePercentage.width).roundToInt(),
            (fullSize.height / 100 * this._sizePercentage.height).roundToInt(),
        ))
        this.imgs = this.initImgs.map { it.scaledTo(this._rect.getSize()) }
        return super.onResize(fullSize)
    }

    public fun click(): Unit {
        if (this.isActive) {
            this.onClickHandlers.toList().forEach { it(this) }
        }
    }
    override fun onUpdate(hoveredElement: UIElement<*>?): Unit {
        if (this._context.mouse.isJustReleased(MouseButton.LEFT) && hoveredElement == this) { this.click() }
        return super.onUpdate(hoveredElement)
    }
}
