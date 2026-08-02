package com.aston728.engine.core

import com.aston728.engine.elements.DirtyFlags

import com.aston728.engine.renderer.Renderer

import com.aston728.engine.layout.Anchor
import com.aston728.engine.layout.Rect
import com.aston728.engine.layout.SizeReference
import com.aston728.engine.renderer.GraphicsContext

import com.aston728.engine.utils.Color
import com.aston728.engine.utils.NamedObject

import com.aston728.engine.types.IntSize
import com.aston728.engine.types.GenericUIElement
import com.aston728.engine.types.DrawSequence

class UI : NamedObject<UI>("Unnamed UI") {
    private var context: EngineContext = defaultEngineContext

    private val elements: MutableSet<GenericUIElement> = mutableSetOf()
    private val anchorMappings: MutableMap<GenericUIElement, MutableSet<GenericUIElement>> = mutableMapOf()
    private val parentMappings: MutableMap<GenericUIElement, MutableSet<GenericUIElement>> = mutableMapOf()

    private var hoveredElement: GenericUIElement? = null

    private var backgroundColor: Color = Color.BLACK

    fun getAnchoredTo(element: GenericUIElement): List<GenericUIElement> = this.anchorMappings.getOrDefault(element, emptySet()).toList()
    fun getChildrenOf(element: GenericUIElement): List<GenericUIElement> = this.parentMappings.getOrDefault(element, emptySet()).toList()
    fun getHoveredElement(): GenericUIElement? = this.hoveredElement

    fun setBackgroundColor(color: Color): UI = apply {
        this.backgroundColor = color
    }

    internal fun attachContext(context: EngineContext): UI = apply {
        if (this.context != context) {
            this.context = context
            this.elements.forEach { it.attachContext(this.context) }
        }
    }
    fun addElement(element: GenericUIElement): UI = apply {
        val didAdd: Boolean = this.elements.add(element)
        if (!didAdd) {
            this.context.logger.warn("UI", "$this already had the element: $element")
        }
        element
            .addDirtyFlag(DirtyFlags.ANCHOR + DirtyFlags.POSITION + DirtyFlags.SIZE + DirtyFlags.SHADER)
            .attachContext(this.context)
    }
    fun removeElement(element: GenericUIElement): UI = apply {
        // TODO: remove elements and handle the consequences
    }
    fun addToWindow(window: Window): UI = apply {
        window.setUI(this)
    }
    fun setStructure(vararg elements: GenericUIElement): UI = apply {
        this.elements.toList().forEach { this.removeElement(it) }
        elements.forEach { this.addElement(it) }
    }

    internal fun checkAnchorLoop(anchor: Anchor): List<GenericUIElement>? {
        val target: GenericUIElement = anchor.getTarget() ?: return null

        val info: MutableList<GenericUIElement> = mutableListOf(target)
        var currentTarget: GenericUIElement? = target.getPositionUnsafe().getAnchorUnsafe().getTarget()
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

            val target: GenericUIElement? = element.getPositionUnsafe().getAnchorUnsafe().getTarget()
            val desiredAnchor: Anchor = element.getPositionUnsafe().getDesiredAnchorUnsafe()

            val loopInfo: List<GenericUIElement>? = this.checkAnchorLoop(desiredAnchor)
            if (loopInfo != null) {
                val loopInfoString = loopInfo.joinToString(" -> ") { it.toString() }
                this.context.logger.error(
                    "ANCHOR LOOP",
                    "Cannot anchor $element to $target\nInfo: $loopInfoString"
                )
                element.discardAnchorChange()
            } else {
                val desiredTarget: GenericUIElement? = desiredAnchor.getTarget()
                this.anchorMappings[target]?.remove(target)
                if (desiredTarget != null) {
                    val anchoredElements: MutableSet<GenericUIElement> = this.anchorMappings.getOrPut(desiredTarget) { mutableSetOf() }
                    anchoredElements.add(desiredTarget)

                    if (desiredTarget !in this.elements) {
                        this.context.logger.warn("UI", "$target is anchored to $desiredTarget which is not in $this")
                        this.addElement(desiredTarget)
                    }
                }

                if (desiredAnchor.isWindowAnchor()) { desiredAnchor.setRect(windowRect) }
                element.commitAnchorChange()
            }
        }
    }
    internal fun checkParentLoop(element: GenericUIElement): List<GenericUIElement>? {
        val info: MutableList<GenericUIElement> = mutableListOf(element)
        var currentParent: GenericUIElement? = element.getDesiredParent()
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

            val parent: GenericUIElement? = element.getParent()
            val desiredParent: GenericUIElement? = element.getDesiredParent()

            val loopInfo: List<GenericUIElement>? = this.checkParentLoop(element)
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
                    val children: MutableSet<GenericUIElement> = this.parentMappings.getOrPut(desiredParent) { mutableSetOf() }
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
        val dirtyElements: MutableSet<GenericUIElement> = mutableSetOf()
        val elementsStack: MutableList<GenericUIElement> = this.elements
            .filter { DirtyFlags.POSITION in it.getDirtyFlags() || DirtyFlags.SIZE in it.getDirtyFlags() }
            .toMutableList()
        while (!elementsStack.isEmpty()) {
            val element: GenericUIElement = elementsStack.removeLast()
            if (element in dirtyElements) { continue } // If B is anchored to A, and they're both dirty, B's children won't be processed twice

            dirtyElements.add(element)
            elementsStack.addAll(this.anchorMappings.getOrDefault(element, mutableSetOf()))
        }

        val sortedDirtyElements: List<GenericUIElement> = dirtyElements.sortedBy { it.getPositionUnsafe().getAnchorUnsafe().getDepth() }
        for (elements in sortedDirtyElements) {
            if (DirtyFlags.SIZE in elements.getDirtyFlags()) {
                val parent: GenericUIElement? = elements.getParent()
                val fullSize: IntSize =
                    if (parent == null || elements.getSizeReference() == SizeReference.WINDOW) windowSize
                    else parent.getRectUnsafe().getSize()
                elements.resize(fullSize)
            }
            elements.reposition()
        }
    }
    internal fun handleDirtyShaders(graphicsContext: GraphicsContext): UI = apply {
        graphicsContext.makeCurrent()
        this.elements.forEach {
            if (DirtyFlags.SHADER in it.getDirtyFlags()) { it.commitShaderChange(graphicsContext.getHandle()) }
        }
    }

    internal fun refreshHoveredElement(): Unit {
        this.hoveredElement = this.elements
            .filter { it.isVisible() && it.isHovered() }
            .maxByOrNull { it.getLayer() }
    }
    internal fun update(): Unit {
        this.elements
            .filter { it.isVisible() }
            .forEach { it.update(this.hoveredElement) }
    }
    internal fun draw(renderer: Renderer): Unit {
        val elements: List<GenericUIElement> = this.elements.sortedBy { it.getLayer() }
        val drawSequence: DrawSequence = elements
            .filter { it.isVisible() }
            .flatMap { it.getDrawSequence(this.hoveredElement) }

        renderer.clearWith(this.backgroundColor)
        renderer.draw(drawSequence)
        elements.forEach { it.draw() } // TODO
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
