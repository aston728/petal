package com.aston728.engine.core.types

import com.aston728.engine.core.Event
import com.aston728.engine.core.elementBase.UIElement
import com.aston728.engine.core.geometry.IntPosition
import com.aston728.engine.core.geometry.Rect
import com.aston728.engine.core.internals.image.InternalImg
import com.aston728.engine.core.internals.image.InternalTextRenderer

public typealias Img = InternalImg
public typealias Imgs = List<Img>
public typealias Rects = List<Rect>
public typealias TextRender = InternalTextRenderer
public typealias FontBytes = ByteArray
public typealias DrawSequence = List<Pair<Img, IntPosition>>

public typealias GenericUIElement = UIElement<*>

public typealias Handler = () -> Unit
public typealias ValueHandler = (Any) -> Unit
public typealias TextHandler = (String) -> Unit
public typealias EventHandler = (Event) -> Unit
public typealias LoggerFormatHandler = (String, String, String) -> String
public typealias LoggerHandler = (String) -> Unit
public typealias ErrorHandler = (String) -> Unit
