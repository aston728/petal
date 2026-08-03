package com.aston728.app

import com.aston728.engine.core.Engine
import com.aston728.engine.core.EngineConfig
import com.aston728.engine.core.EngineMode
import com.aston728.engine.core.FileDropEvent
import com.aston728.engine.core.Window
import com.aston728.engine.core.UI
import com.aston728.engine.elements.Button
import com.aston728.engine.renderer.ShaderUniformHandle
import com.aston728.engine.layout.Coordinate
import com.aston728.engine.utils.Color
import com.aston728.engine.layout.Anchor
import com.aston728.engine.renderer.GraphicsApi
import com.aston728.engine.renderer.ShaderSpec
import com.aston728.engine.renderer.ShaderVertexAttributeHandle
import com.aston728.engine.renderer.ShaderVertexData
import com.aston728.engine.types.IntOffset
import com.aston728.engine.types.IntSize
import com.aston728.engine.types.SizePercentage
import com.aston728.engine.types.Vec2
import com.aston728.engine.types.Vec3

fun main(): Unit {
    val engineConfig: EngineConfig = EngineConfig(
        EngineMode.DEBUG,
        GraphicsApi.OPEN_GL
    )
    val engine: Engine = Engine.create(engineConfig)
    engine.init()

    engine
        .setFpsCap(60)

    val shaderPosition = ShaderVertexAttributeHandle.vec2()
    val shaderColor = ShaderUniformHandle.vec3()
    val testShader1: ShaderSpec = ShaderSpec()
        .setName("Shader1")
        .addVertexAttribute("position", shaderPosition)
        .addUniform("uColor", shaderColor, defaultValue =  Vec3(1.0f, 0.0f, 0.0f))
        .setVertexShaderSource("gl_Position = vec4(position, 0.0, 1.0);")
        .setFragmentShaderSource("oColor = vec4(uColor, 1.0);")

    val testWindow1: Window = engine.createWindow()
        .setName("Test Window 1")
        .setSize(IntSize(800, 800))
    val testUI1: UI = UI()
        .setName("Test UI 1")
        .setBackgroundColor(Color.CYAN)
    val testButton1: Button = Button()
        .setName("Test Button 1")
        .setPosition(
            Anchor.toWindow(Coordinate.CENTER),
            Coordinate.CENTER, IntOffset(0, 0)
        )
        .setShader(testShader1, data = listOf(
            ShaderVertexData(shaderPosition, arrayOf(Vec2(-1.0f, -1.0f), Vec2(-0.5f, -1.0f), Vec2(-1.0f, -0.5f), Vec2(-0.5f, -0.5f)))
        ))
        .setSizePercentage(SizePercentage(10.0, 10.0))
        .addOnClickHandler({ testWindow1.setUtility(!testWindow1.isUtility()!!) })
    val testButton2: Button = Button()
        .setName("Test Button 2")
        .setPosition(
            Anchor.toWindow(Coordinate.TOP_LEFT),
            Coordinate.TOP_LEFT, IntOffset(0, 0)
        )
        .setShader(testShader1, data = listOf(
            ShaderVertexData(shaderPosition, arrayOf(Vec2(0.0f, 0.5f), Vec2(0.5f, 0.5f), Vec2(0.0f, 0.0f), Vec2(0.5f, 0.0f)))
        ))
        .setSizePercentage(SizePercentage(10.0, 10.0))

    val testWindow2: Window = engine.createWindow()
        .setName("Test Window 2")
    val testUI2: UI = UI()
        .setName("Test UI 2")
        .setBackgroundColor(Color.PURPLE)
    val testButton3: Button = Button()
        .setName("Test Button 2")
        .setPosition(
            Anchor.toWindow(Coordinate.CENTER),
            Coordinate.CENTER, IntOffset(0, 0)
        )
        .setSizePercentage(SizePercentage(10.0, 10.0))
        .addOnClickHandler({ println("Clicked") })

    engine.setStructure(
        testWindow1.setStructure(
            testUI1.setStructure(
                testButton1, testButton2
            )
        ),
    )
    engine.run()

    engine.shutdown()
}
