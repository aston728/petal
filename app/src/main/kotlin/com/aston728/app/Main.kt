package com.aston728.app

import com.aston728.engine.core.Engine
import com.aston728.engine.core.EngineConfig
import com.aston728.engine.core.EngineMode
import com.aston728.engine.core.Window
import com.aston728.engine.core.UI
import com.aston728.engine.elements.Button
import com.aston728.engine.layout.Coordinate
import com.aston728.engine.utils.Color
import com.aston728.engine.internals.devices.Cursor
import com.aston728.engine.internals.devices.Key
import com.aston728.engine.layout.Anchor
import com.aston728.engine.renderer.GraphicsApi
import com.aston728.engine.types.IntOffset
import com.aston728.engine.types.IntPosition
import com.aston728.engine.types.SizePercentage

fun main(): Unit {
    val engineConfig: EngineConfig = EngineConfig(
        EngineMode.DEBUG,
        GraphicsApi.OPEN_GL
    )
    val engine: Engine = Engine.create(engineConfig)
    engine.init()

    engine
        .setFpsCap(60)

    val testWindow1: Window = engine.createWindow()
        .setName("Test Window 1")
    val testUI1: UI = UI()
        .setName("Test UI 1")
        .setBackgroundColor(Color.CYAN)
    val testButton1: Button = Button()
        .setName("Test Button 1")
        .setPosition(
            Anchor.toWindow(Coordinate.CENTER),
            Coordinate.CENTER, IntOffset(0, 0)
        )
        .setSizePercentage(SizePercentage(10.0, 10.0))
        .addOnClickHandler({ println("Clicked") })

    val testWindow2: Window = engine.createWindow()
        .setName("Test Window 2")
    val testUI2: UI = UI()
        .setName("Test UI 2")
        .setBackgroundColor(Color.RED)

    engine.setStructure(
        testWindow1.setStructure(
            testUI1.setStructure(
                testButton1,
            )
        ),
        testWindow2.setStructure(testUI2)
    )
    engine.run()

    engine.shutdown()
}
