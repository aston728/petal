package com.aston728.app

import com.aston728.engine.core.*
import com.aston728.engine.core.elementBase.layout.Anchor
import com.aston728.engine.core.geometry.Coordinate
import com.aston728.engine.core.geometry.IntOffset
import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.geometry.SizePercentage
import com.aston728.engine.core.internals.Image
import com.aston728.engine.core.math.Vec2
import com.aston728.engine.core.rendererBase.*
import com.aston728.engine.core.utils.Color
import com.aston728.engine.elements.Button

fun main(): Unit {
    val engineConfig: EngineConfig = EngineConfig(
        EngineMode.DEBUG,
        GraphicsApi.OPEN_GL,
    )
    val engine: Engine = Engine.create(engineConfig)
    engine.init()

    engine
        .setFpsCap(60)

    val img = Image("src/main/resources", "test.png", assetLoader = engine.getContext().assetLoader, shouldHotReload = true)
    val sampler = ShaderSampledImage2D(img)

    val shaderTextureCoord = ShaderVertexAttributeHandle.vec2("ITextureCoord")
    val shaderSampler = ShaderUniformHandle.img2D("a")
    val testShader1: ShaderSpec = ShaderSpec()
        .setName("Shader1")
        .addVertexAttribute(shaderTextureCoord, listOf(Vec2(0.0f, 0.0f), Vec2(1.0f, 0.0f), Vec2(0.0f, 1.0f), Vec2(1.0f, 1.0f)))
        .addIntermediateAttribute("textureCoord", ShaderAttributeType.Vec2)
        .addUniform(shaderSampler, sampler)
        .addVertexFooter("textureCoord = ITextureCoord;")
        .setFragmentShaderBody("oColor = texture(a, textureCoord);")

    val testWindow1: Window = engine.createWindow()
        .setName("Test Window 1")
        .setTitle("a")
        .setSize(IntSize(800, 800))
    val testUI1: UI = UI()
        .setName("Test UI 1")
        .setBackgroundColor(Color.CYAN)
    val testButton1: Button = Button()
        .setName("Test Button 1")
        .setPosition(
            Anchor.toWindow(Coordinate.TOP_LEFT),
            Coordinate.TOP_LEFT, IntOffset(0, 0)
        )
        .setShader(testShader1)
        .setSizePercentage(SizePercentage(20.0, 10.0))

    val testWindow2: Window = engine.createWindow()
        .setName("Test Window 2")
        .setTitle("b")
        .setSize(IntSize(800, 800))
    val testUI2: UI = UI()
        .setName("Test UI 2")
        .setBackgroundColor(Color.PURPLE)
    val testButton2: Button = Button()
        .setName("Test Button 2")
        .setPosition(
            Anchor.toWindow(Coordinate.CENTER),
            Coordinate.CENTER, IntOffset(0, 0)
        )
        .setShader(testShader1)
        .setSizePercentage(SizePercentage(10.0, 10.0))
        .addOnClickHandler({ println("Clicked") })

    engine.setStructure(
        testWindow1.setStructure(
            testUI1.setStructure(
                testButton1,
            )
        ),
        testWindow2.setStructure(
            testUI2.setStructure(
                testButton2,
            )
        )
    )
    engine.run()

    engine.shutdown()
}
