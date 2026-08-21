package com.aston728.engine.core.elementBase

import com.aston728.engine.core.EngineContext
import com.aston728.engine.core.UI
import com.aston728.engine.core.defaultEngineContext
import com.aston728.engine.core.elementBase.layout.Anchor
import com.aston728.engine.core.elementBase.layout.AnchoredPosition
import com.aston728.engine.core.elementBase.layout.SizeReference
import com.aston728.engine.core.geometry.*
import com.aston728.engine.core.internals.devices.Cursor
import com.aston728.engine.core.internals.image.Image
import com.aston728.engine.core.rendererBase.*
import com.aston728.engine.core.utils.NamedObject

public abstract class UIElement<T : UIElement<T>>(name: String) : NamedObject<T>(name) {
    private companion object {
        private val DEFAULT_SHADER_HANDLE: ShaderInstanceHandle = ShaderInstanceHandle(BlankShader("Element Shader"), 0)
    }

    protected var _context: EngineContext = defaultEngineContext
        private set

    private val anchoredPosition: AnchoredPosition = AnchoredPosition(
        Anchor.toWindow(Coordinate.TOP_LEFT),
        Coordinate.TOP_LEFT, IntOffset(0, 0)
    )
    protected var _sizePercentage: SizePercentage = SizePercentage(10.0, 5.0)
        private set
    protected val _rect: Rect = Rect()
    private var layer: Double = 0.0

    private var shaderSpec: ShaderSpec = ShaderSpec()
    private var shaderData: List<ShaderData> = emptyList()
    protected var _shaderInstanceHandle: ShaderInstanceHandle = UIElement.DEFAULT_SHADER_HANDLE
        private set

    private var parent: UIElement<*>? = null
    private var desiredParent: UIElement<*>? = this.parent

    private var isVisible: Boolean = true
    private var sizeReference: SizeReference = SizeReference.PARENT
    private var dirtyFlags: DirtyFlags = DirtyFlags.none()

    internal fun getRectUnsafe(): Rect = this._rect
    internal fun getPositionUnsafe(): AnchoredPosition = this.anchoredPosition
    internal fun getDesiredParent(): UIElement<*>? = this.desiredParent
    internal fun getDirtyFlags(): DirtyFlags = this.dirtyFlags

    public fun getPosition(): AnchoredPosition = this.anchoredPosition.copy()
    public fun getSizePercentage(): SizePercentage = this._sizePercentage
    public fun getLayer(): Double = this.layer
    public fun getShader(): Shader = this._shaderInstanceHandle.shader
    public fun getShaderInstance(): ShaderInstanceHandle = this._shaderInstanceHandle
    public fun getParent(): UIElement<*>? = this.parent
    public fun isVisible(): Boolean = this.isVisible
    public fun getSizeReference(): SizeReference = this.sizeReference

    internal fun addDirtyFlag(flag: DirtyFlags): T = this.self {
        this.dirtyFlags += flag
    }

    public fun setPosition(anchor: Anchor, coordinate: Coordinate, offset: IntOffset): T = this.self {
        this.anchoredPosition.setAnchor(anchor).setCoordinate(coordinate).setOffset(offset)
        this.dirtyFlags += DirtyFlags.ANCHOR + DirtyFlags.POSITION
    }
    public fun setPositionOffset(offset: IntOffset): T = this.self {
        this.anchoredPosition.setOffset(offset)
        this.dirtyFlags += DirtyFlags.POSITION
    }
    public fun setSizePercentage(percentage: SizePercentage): T = this.self {
        this._sizePercentage = percentage
        this.addDirtyFlag(DirtyFlags.SIZE)
    }
    public fun setSizeReference(reference: SizeReference): T = this.self {
        this.sizeReference = reference
    }
    public fun setLayer(layer: Double): T = this.self {
        this.layer = layer
    }
    public open fun setShader(vararg specs: ShaderSpec, data: List<ShaderData> = emptyList()): T = this.self {
        this.shaderSpec = this._context.shaderProvider.specGetOrMerge(*specs)
        this.shaderData = data
        this.dirtyFlags += DirtyFlags.SHADER
    }
    public fun setToDefaultShader(): T = this.self {
        this.setShader(ShaderSpec())
    }
    public fun setParent(parent: UIElement<*>?): T = this.self {
        this.desiredParent = parent
        this.dirtyFlags += DirtyFlags.RELATIONSHIP
    }
    public fun addChild(child: UIElement<*>): T = this.self {
        child.setParent(this)
    }
    public fun setVisible(isVisible: Boolean): T = this.self {
        this.isVisible = isVisible
    }
    public fun toggleVisibility(): T = this.setVisible(!this.isVisible)

    internal fun attachContext(context: EngineContext): T = this.self {
        this._context = context
    }
    public fun addToUI(ui: UI): T = this.self {
        ui.addElement(this)
    }

    public open fun isHovered(): Boolean = this._rect.isCollidingWith(this._context.mouse.getPosition())
    public open fun getCursorType(): Cursor = Cursor.ARROW
    protected open fun onCommitAnchorChange(): Unit {
        this.anchoredPosition.commitAnchorChange()
        this.dirtyFlags -= DirtyFlags.ANCHOR
    }
    protected open fun onDiscardAnchorChange(): Unit {
        this.dirtyFlags -= DirtyFlags.ANCHOR
    }
    protected open fun onCommitParentChange(): Unit {
        this.parent = this.desiredParent
        this.desiredParent = null
        this.dirtyFlags -= DirtyFlags.RELATIONSHIP
    }
    protected open fun onDiscardParentChange(): Unit {
        this.desiredParent = null
        this.dirtyFlags -= DirtyFlags.RELATIONSHIP
    }
    protected open fun onReposition(): Unit {
        this.anchoredPosition.applyTo(this._rect)
        this.dirtyFlags -= DirtyFlags.POSITION
    }
    protected open fun onResize(fullSize: IntSize): Unit {
        this.dirtyFlags -= DirtyFlags.SIZE
    }
    protected open fun onCommitShaderChange(windowHandle: Long): Unit {
        var instanceHandle: ShaderInstanceHandle? = this._context.shaderProvider.acquire(windowHandle, this.shaderSpec)
        if (instanceHandle == null && this._shaderInstanceHandle.shader is BlankShader) {
            this.setToDefaultShader()
            instanceHandle = this._context.shaderProvider.acquire(windowHandle, this.shaderSpec)
            this.shaderData = emptyList()
        }

        if (instanceHandle != null) {
            this._context.shaderProvider.release(this._shaderInstanceHandle)
            this._shaderInstanceHandle = instanceHandle
            this.shaderData.forEach { it.applyTo(this._shaderInstanceHandle) }
        }
        this.dirtyFlags -= DirtyFlags.SHADER
    }
    protected open fun computeDrawSequence(hoveredElement: UIElement<*>?): List<Pair<Image, IntPosition>> = emptyList()
    protected open fun onUpdate(hoveredElement: UIElement<*>?): Unit {}

    internal fun commitAnchorChange(): Unit = this.onCommitAnchorChange()
    internal fun discardAnchorChange(): Unit = this.onDiscardAnchorChange()
    internal fun commitParentChange(): Unit = this.onCommitParentChange()
    internal fun discardParentChange(): Unit = this.onDiscardParentChange()
    internal fun reposition(): Unit =  this.onReposition()
    internal fun resize(fullSize: IntSize): Unit = this.onResize(fullSize)
    internal fun commitShaderChange(windowHandle: Long): Unit = this.onCommitShaderChange(windowHandle)
    internal fun getDrawSequence(hoveredElement: UIElement<*>?): List<Pair<Image, IntPosition>> = this.computeDrawSequence(hoveredElement)
    internal fun update(hoveredElement: UIElement<*>?): Unit = this.onUpdate(hoveredElement)
}
