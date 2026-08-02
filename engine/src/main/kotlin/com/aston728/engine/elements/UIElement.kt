package com.aston728.engine.elements

import com.aston728.engine.core.EngineContext
import com.aston728.engine.core.defaultEngineContext
import com.aston728.engine.core.UI

import com.aston728.engine.layout.AnchoredPosition
import com.aston728.engine.layout.Anchor
import com.aston728.engine.layout.Rect
import com.aston728.engine.layout.SizeReference
import com.aston728.engine.layout.Coordinate

import com.aston728.engine.utils.NamedObject

import com.aston728.engine.renderer.ShaderData
import com.aston728.engine.renderer.ShaderSpec

import com.aston728.engine.internals.devices.Cursor
import com.aston728.engine.renderer.Shader
import com.aston728.engine.renderer.ShaderInstanceHandle

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.IntOffset
import com.aston728.engine.types.DrawSequence
import com.aston728.engine.types.GenericUIElement

sealed class UIElement<T : UIElement<T>> (name: String) : NamedObject<T>(name) {
    protected var _context: EngineContext = defaultEngineContext
        private set

    private val anchoredPosition: AnchoredPosition = AnchoredPosition(
        Anchor.toWindow(Coordinate.TOP_LEFT),
        Coordinate.TOP_LEFT, IntOffset(0, 0)
    )
    protected val _rect: Rect = Rect()
    private var layer: Double = 0.0

    private var shaderSpec: ShaderSpec = ShaderSpec()
    private var shaderData: Array<ShaderData> = emptyArray()
    protected var _shaderInstanceHandle: ShaderInstanceHandle = this._context.shaderProvider.acquirePlaceholder()
        private set

    private var parent: GenericUIElement? = null
    private var desiredParent: GenericUIElement? = this.parent

    private var isVisible: Boolean = true
    private var sizeReference: SizeReference = SizeReference.PARENT
    private var dirtyFlags: DirtyFlags = DirtyFlags.none()

    internal fun getRectUnsafe(): Rect = this._rect
    internal fun getPositionUnsafe(): AnchoredPosition = this.anchoredPosition
    internal fun getDesiredParent(): GenericUIElement? = this.desiredParent
    internal fun getDirtyFlags(): DirtyFlags = this.dirtyFlags

    fun getPosition(): AnchoredPosition = this.anchoredPosition.copy()
    fun getLayer(): Double = this.layer
    fun getShader(): Shader = this._shaderInstanceHandle.shader
    fun getShaderInstance(): ShaderInstanceHandle = this._shaderInstanceHandle
    fun getParent(): GenericUIElement? = this.parent
    fun isVisible(): Boolean = this.isVisible
    fun getSizeReference(): SizeReference = this.sizeReference

    internal fun addDirtyFlag(flag: DirtyFlags): T = this.self {
        this.dirtyFlags += flag
    }

    fun setPosition(anchor: Anchor, coordinate: Coordinate, offset: IntOffset): T = this.self {
        this.anchoredPosition.setAnchor(anchor).setCoordinate(coordinate).setOffset(offset)
        this.dirtyFlags += DirtyFlags.ANCHOR + DirtyFlags.POSITION
    }
    fun setPositionOffset(offset: IntOffset): T = this.self {
        this.anchoredPosition.setOffset(offset)
        this.dirtyFlags += DirtyFlags.POSITION
    }
    fun setSizeReference(reference: SizeReference): T = this.self {
        this.sizeReference = reference
    }
    fun setLayer(layer: Double): T = this.self {
        this.layer = layer
    }
    open fun setShader(vararg specs: ShaderSpec, data: Array<ShaderData> = emptyArray()): T = this.self {
        this.shaderSpec = ShaderSpec.merge(*specs)
        this.shaderData = data
        this.dirtyFlags += DirtyFlags.SHADER
    }
    fun setParent(parent: GenericUIElement?): T = this.self {
        this.desiredParent = parent
        this.dirtyFlags += DirtyFlags.RELATIONSHIP
    }
    fun addChild(child: GenericUIElement): T = this.self {
        child.setParent(this)
    }
    fun setVisible(isVisible: Boolean): T = this.self {
        this.isVisible = isVisible
    }
    fun toggleVisibility(): T = this.setVisible(!this.isVisible)

    internal fun attachContext(context: EngineContext): T = this.self {
        this._context = context
    }
    fun addToUI(ui: UI): T = this.self {
        ui.addElement(this)
    }

    internal open fun isHovered(): Boolean = false
    internal open fun getCursorType(): Cursor = Cursor.ARROW
    internal open fun commitAnchorChange(): Unit {
        this.anchoredPosition.commitAnchorChange()
        this.dirtyFlags -= DirtyFlags.ANCHOR
    }
    internal open fun discardAnchorChange(): Unit {
        this.dirtyFlags -= DirtyFlags.ANCHOR
    }
    internal open fun commitParentChange(): Unit {
        this.parent = this.desiredParent
        this.desiredParent = null
        this.dirtyFlags -= DirtyFlags.RELATIONSHIP
    }
    internal open fun discardParentChange(): Unit {
        this.desiredParent = null
        this.dirtyFlags -= DirtyFlags.RELATIONSHIP
    }
    internal open fun reposition(): Unit {
        this.anchoredPosition.applyTo(this._rect)
        this.dirtyFlags -= DirtyFlags.POSITION
    }
    internal open fun resize(fullSize: IntSize): Unit {
        this.dirtyFlags -= DirtyFlags.SIZE
    }
    internal open fun commitShaderChange(contextHandle: Long): Unit {
        this._context.shaderProvider.release(this._shaderInstanceHandle)
        this._shaderInstanceHandle = this._context.shaderProvider.acquire(contextHandle, this.shaderSpec)
        this.shaderData.forEach { it.applyTo(this._shaderInstanceHandle) }
        this.dirtyFlags -= DirtyFlags.SHADER
    }
    internal open fun getDrawSequence(hoveredElement: GenericUIElement?): DrawSequence = emptyList()
    internal open fun update(hoveredElement: GenericUIElement?): Unit {}

    internal fun draw(): Unit {  // TODO
        this._shaderInstanceHandle.draw()
    }
}
