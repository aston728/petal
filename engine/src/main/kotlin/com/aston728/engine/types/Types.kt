package com.aston728.engine.types

import com.aston728.engine.core.Event

import com.aston728.engine.elements.UIElement

import com.aston728.engine.layout.Rect

import com.aston728.engine.internals.types.InternalImg
import com.aston728.engine.internals.types.InternalTextRenderer

typealias Img = InternalImg
typealias Imgs = List<Img>
typealias Rects = List<Rect>
typealias TextRender = InternalTextRenderer
typealias FontBytes = ByteArray
typealias DrawSequence = List<Pair<Img, IntPosition>>

typealias GenericUIElement = UIElement<*>

typealias Handler = () -> Unit
typealias ValueHandler = (Any) -> Unit
typealias TextHandler = (String) -> Unit
typealias EventHandler = (Event) -> Unit
typealias LoggerFormatHandler = (String, String, String) -> String
typealias LoggerHandler = (String) -> Unit
typealias ErrorHandler = (String) -> Unit
