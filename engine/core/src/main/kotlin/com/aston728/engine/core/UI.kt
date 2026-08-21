package com.aston728.engine.core

import com.aston728.engine.core.elementBase.DirtyFlags
import com.aston728.engine.core.elementBase.UIElement
import com.aston728.engine.core.elementBase.layout.Anchor
import com.aston728.engine.core.elementBase.layout.AnchorType
import com.aston728.engine.core.elementBase.layout.SizeReference
import com.aston728.engine.core.geometry.IntPosition
import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.geometry.Rect
import com.aston728.engine.core.internals.image.Image
import com.aston728.engine.core.rendererBase.Shader
import com.aston728.engine.core.utils.Color
import com.aston728.engine.core.utils.NamedObject

public class UI : NamedObject<UI>("Unnamed UI") {
    private var context: EngineContext = defaultEngineContext

    private val elements: MutableSet<UIElement<*>> = mutableSetOf()
    private val anchorMappings: MutableMap<UIElement<*>, MutableSet<UIElement<*>>> = mutableMapOf()
    private val parentMappings: MutableMap<UIElement<*>, MutableSet<UIElement<*>>> = mutableMapOf()

    private var hoveredElement: UIElement<*>? = null

    private var backgroundColor: Color = Color.BLACK

    public fun getAnchoredTo(element: UIElement<*>): List<UIElement<*>> = this.anchorMappings.getOrDefault(element, emptySet()).toList()
    public fun getChildrenOf(element: UIElement<*>): List<UIElement<*>> = this.parentMappings.getOrDefault(element, emptySet()).toList()
    public fun getHoveredElement(): UIElement<*>? = this.hoveredElement

    public fun setBackgroundColor(color: Color): UI = apply {
        this.backgroundColor = color
    }

    internal fun attachContext(context: EngineContext): UI = apply {
        if (this.context != context) {
            this.context = context
            this.elements.forEach { it.attachContext(this.context) }
        }
    }
    public fun addElement(element: UIElement<*>): UI = apply {
        val didAdd: Boolean = this.elements.add(element)
        if (!didAdd) {
            this.context.logger.warn("UI", "$this already had the element: $element")
        }
        element
            .addDirtyFlag(DirtyFlags.ANCHOR + DirtyFlags.POSITION + DirtyFlags.SIZE + DirtyFlags.SHADER)
            .attachContext(this.context)
    }
    public fun removeElement(element: UIElement<*>): UI = apply {
        // TODO: remove elements and handle the consequences
    }
    public fun addToWindow(window: Window): UI = apply {
        window.setUI(this)
    }
    public fun setStructure(vararg elements: UIElement<*>): UI = apply {
        this.elements.toList().forEach { this.removeElement(it) }
        elements.forEach { this.addElement(it) }
    }

    private fun checkAnchorLoop(anchor: Anchor): List<UIElement<*>>? {
        val target: UIElement<*> = anchor.getTarget() ?: return null

        val info: MutableList<UIElement<*>> = mutableListOf(target)
        var currentTarget: UIElement<*>? = target.getPositionUnsafe().getAnchorUnsafe().getTarget()
        while (currentTarget != null) {
            info.add(currentTarget)
            if (target == currentTarget) { return info }
            currentTarget = currentTarget.getPositionUnsafe().getAnchorUnsafe().getTarget()
        }

        return null
    }
    internal fun handleDirtyAnchors(windowRect: Rect): UI = apply {
        for (element in this.elements) {
            if (DirtyFlags.ANCHOR !in element.getDirtyFlags()) { continue }

            val target: UIElement<*>? = element.getPositionUnsafe().getAnchorUnsafe().getTarget()
            val desiredAnchor: Anchor = element.getPositionUnsafe().getDesiredAnchorUnsafe()

            val loopInfo: List<UIElement<*>>? = this.checkAnchorLoop(desiredAnchor)
            if (loopInfo != null) {
                val loopInfoString = loopInfo.joinToString(" -> ") { it.toString() }
                this.context.logger.error(
                    "ANCHOR LOOP",
                    "Cannot anchor $element to $target\nInfo: $loopInfoString"
                )
                element.discardAnchorChange()
            } else {
                val desiredTarget: UIElement<*>? = desiredAnchor.getTarget()
                this.anchorMappings[target]?.remove(target)
                if (desiredTarget != null) {
                    val anchoredElements: MutableSet<UIElement<*>> = this.anchorMappings.getOrPut(desiredTarget) { mutableSetOf() }
                    anchoredElements.add(desiredTarget)

                    if (desiredTarget !in this.elements) {
                        this.context.logger.warn("UI", "$target is anchored to $desiredTarget which is not in $this")
                        this.addElement(desiredTarget)
                    }
                }

                if (desiredAnchor.getType() == AnchorType.WINDOW) { desiredAnchor.setRect(windowRect) }
                element.commitAnchorChange()
            }
        }
    }
    private fun checkParentLoop(element: UIElement<*>): List<UIElement<*>>? {
        val info: MutableList<UIElement<*>> = mutableListOf(element)
        var currentParent: UIElement<*>? = element.getDesiredParent()
        while (currentParent != null) {
            info.add(currentParent)
            if (element == currentParent) { return info }
            currentParent = currentParent.getParent()
        }

        return null
    }
    internal fun handleDirtyRelationship(): UI = apply {
        for (element in this.elements) {
            if (DirtyFlags.RELATIONSHIP !in element.getDirtyFlags()) { continue }

            val parent: UIElement<*>? = element.getParent()
            val desiredParent: UIElement<*>? = element.getDesiredParent()

            val loopInfo: List<UIElement<*>>? = this.checkParentLoop(element)
            if (loopInfo != null) {
                val loopInfoString = loopInfo.joinToString(" -> ") { it.toString() }
                this.context.logger.error(
                    "PARENT LOOP",
                    "Cannot set parent of $element to $desiredParent\nInfo: $loopInfoString"
                )
                element.discardParentChange()
            } else {
                this.parentMappings[parent]?.remove(element)
                if (desiredParent != null) {
                    val children: MutableSet<UIElement<*>> = this.parentMappings.getOrPut(desiredParent) { mutableSetOf() }
                    children.add(desiredParent)

                    if (desiredParent !in this.elements) {
                        this.context.logger.warn("UI", "$element is a child of $desiredParent which is not in $this")
                        this.addElement(desiredParent)
                    }
                }

                if (element.getSizeReference() == SizeReference.PARENT) { element.addDirtyFlag(DirtyFlags.SIZE) }
                element.commitParentChange()
            }
        }
    }
    internal fun handleDirtyLayouts(windowSize: IntSize): UI = apply {
        val dirtyElements: MutableSet<UIElement<*>> = mutableSetOf()
        val elementsStack: MutableList<UIElement<*>> = this.elements
            .filter { DirtyFlags.POSITION in it.getDirtyFlags() || DirtyFlags.SIZE in it.getDirtyFlags() }
            .toMutableList()
        while (!elementsStack.isEmpty()) {
            val element: UIElement<*> = elementsStack.removeLast()
            if (element in dirtyElements) { continue } // If B is anchored to A, and they're both dirty, B's children won't be processed twice

            dirtyElements.add(element)
            elementsStack.addAll(this.anchorMappings.getOrDefault(element, mutableSetOf()))
        }

        val sortedDirtyElements: List<UIElement<*>> = dirtyElements.sortedBy { it.getPositionUnsafe().getAnchorUnsafe().getDepth() }
        for (element in sortedDirtyElements) {
            if (DirtyFlags.SIZE in element.getDirtyFlags()) {
                val parent: UIElement<*>? = element.getParent()
                val fullSize: IntSize =
                    if (parent == null || element.getSizeReference() == SizeReference.WINDOW) { windowSize }
                    else { parent.getRectUnsafe().getSize() }
                element.resize(fullSize)
            }
            element.reposition()
        }
    }
    internal fun handleDirtyShaders(windowHandle: Long): UI = apply {
        for (element in this.elements) {
            if (DirtyFlags.SHADER in element.getDirtyFlags()) { element.commitShaderChange(windowHandle) }
        }
    }

    internal fun refreshHoveredElement(): Unit {
        this.hoveredElement = this.elements
            .filter { it.isVisible() && it.isHovered() }
            .maxByOrNull { it.getLayer() }
    }
    internal fun update(): Unit {
        this.elements.forEach {
            if (it.isVisible()) { it.update(this.hoveredElement) }
        }
    }
    internal fun getShaders(): List<Shader> { // TODO
        val elements: List<UIElement<*>> = this.elements.sortedBy { it.getLayer() }
        val drawSequence: List<Pair<Image, IntPosition>> = elements
            .filter { it.isVisible() }
            .flatMap { it.getDrawSequence(this.hoveredElement) }

        return elements.map { it.getShader() }
    }

    internal fun onFocus(): Unit {
        this.refreshHoveredElement()
    }
    internal fun onUnfocus(): Unit {
        this.hoveredElement = null
    }
    internal fun onResize(): Unit {
        this.elements.forEach { it.addDirtyFlag(DirtyFlags.SIZE) }
    }
    internal fun onWindowAttach(): Unit {
        this.elements.forEach { it.addDirtyFlag(DirtyFlags.SHADER) }
    }
}
