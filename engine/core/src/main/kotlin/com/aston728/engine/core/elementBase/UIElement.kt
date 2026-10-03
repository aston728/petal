package com.aston728.engine.core.elementBase

import com.aston728.engine.core.EngineContext
import com.aston728.engine.core.UI
import com.aston728.engine.core.defaultEngineContext
import com.aston728.engine.core.elementBase.layout.Anchor
import com.aston728.engine.core.elementBase.layout.AnchoredPosition
import com.aston728.engine.core.elementBase.layout.SizeReference
import com.aston728.engine.core.geometry.*
import com.aston728.engine.core.internals.devices.Cursor
import com.aston728.engine.core.rendererBase.*
import com.aston728.engine.core.utils.Handle
import com.aston728.engine.core.utils.NamedObject

public abstract class UIElement<T : UIElement<T>>(name: String) : NamedObject<T>(name) {
    private companion object {
        private val BLANK_SHADER_SPEC: ShaderSpec = ShaderSpec()
        private val BLANK_SHADER_ATTRIBUTES_DATA: List<ShaderData> = emptyList()
        private val PLACEHOLDER_SHADER_HANDLE: ShaderInstanceHandle = ShaderInstanceHandle(BlankShader("Element Shader"), 0, errorCallback = {})
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

    private var shaderSpec: ShaderSpec = UIElement.BLANK_SHADER_SPEC
    private var shaderAttributesData: List<ShaderData> = UIElement.BLANK_SHADER_ATTRIBUTES_DATA
    protected var _shaderInstanceHandle: ShaderInstanceHandle = UIElement.PLACEHOLDER_SHADER_HANDLE
        private set

    private var parent: UIElement<*>? = null
    private var desiredParent: UIElement<*>? = this.parent

    private var isVisible: Boolean = true
    private var sizeReference: SizeReference = SizeReference.PARENT
    private var dirtyFlags: DirtyFlags = DirtyFlags.none()

    private var shaderChangeHandlers: MutableList<(ShaderInstanceHandle, ShaderInstanceHandle) -> Unit> = mutableListOf()

    init {
        this.setToDefaultShader()
    }

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

    internal fun addDirtyFlag(flag: DirtyFlags): Unit {
        this.dirtyFlags += flag
    }

    public fun setPosition(anchor: Anchor, coordinate: Coordinate, offset: IntOffset): T = this.self {
        this.anchoredPosition.setAnchor(anchor)
        this.anchoredPosition.setCoordinate(coordinate)
        this.anchoredPosition.setOffset(offset)
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
        this.shaderSpec = ShaderSpec.merge(*specs)
        this.shaderAttributesData = data
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
    public fun addShaderChangeHandler(handler: (ShaderInstanceHandle, ShaderInstanceHandle) -> Unit, handle: Handle? = null): Unit {
        val wrapper: (ShaderInstanceHandle, ShaderInstanceHandle) -> Unit = { previousInstance, newInstance -> handler(previousInstance, newInstance) }
        this.shaderChangeHandlers.add(wrapper)
        handle?.setOnRemoveHandler { this.shaderChangeHandlers.remove(wrapper) }
    }

    internal fun attachContext(context: EngineContext): Unit {
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
    protected open fun onUpdate(hoveredElement: UIElement<*>?): Unit {}

    internal fun commitAnchorChange(): Unit = this.onCommitAnchorChange()
    internal fun discardAnchorChange(): Unit = this.onDiscardAnchorChange()
    internal fun commitParentChange(): Unit = this.onCommitParentChange()
    internal fun discardParentChange(): Unit = this.onDiscardParentChange()
    internal fun reposition(): Unit =  this.onReposition()
    internal fun resize(fullSize: IntSize): Unit = this.onResize(fullSize)
    internal fun commitShaderChange(
        acquireInstanceHandle: (ShaderSpec) -> ShaderInstanceHandle?,
        releaseInstanceHandle: (ShaderInstanceHandle) -> Unit
    ): Unit {
        var instanceHandle: ShaderInstanceHandle? = acquireInstanceHandle(this.shaderSpec)
        if (instanceHandle == null && this._shaderInstanceHandle == UIElement.PLACEHOLDER_SHADER_HANDLE) {
            this.setToDefaultShader()
            instanceHandle = acquireInstanceHandle(this.shaderSpec)
        }

        if (instanceHandle != null) {
            val previousHandle: ShaderInstanceHandle = this._shaderInstanceHandle
            releaseInstanceHandle(previousHandle)

            this._shaderInstanceHandle = instanceHandle.setName("ShaderInstanceHandle($this,${instanceHandle.shader})")
            this.shaderAttributesData.forEach { it.applyTo(this._shaderInstanceHandle) }
            this.shaderChangeHandlers.toList().forEach { it(previousHandle, this._shaderInstanceHandle) }
        }
        this.shaderSpec = UIElement.BLANK_SHADER_SPEC
        this.shaderAttributesData = UIElement.BLANK_SHADER_ATTRIBUTES_DATA
        this.dirtyFlags -= DirtyFlags.SHADER
    }
    internal fun update(hoveredElement: UIElement<*>?): Unit = this.onUpdate(hoveredElement)
}
