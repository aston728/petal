package com.aston728.app

import com.aston728.engine.core.*
import com.aston728.engine.core.elementBase.layout.Anchor
import com.aston728.engine.core.geometry.Coordinate
import com.aston728.engine.core.geometry.IntOffset
import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.geometry.SizePercentage
import com.aston728.engine.core.internals.image.Image
import com.aston728.engine.core.math.HVec4
import com.aston728.engine.core.math.Half
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

    val img = engine.getContext().assetManager.loadImage("test.png")
    val sampler = ShaderSampledImage2D(img)

    val shaderPosition: ShaderVertexAttributeHandle<Vec2> = ShaderVertexAttributeHandle.vec2()
    val shaderTextureCoord = ShaderVertexAttributeHandle.vec2()
    val shaderColor = ShaderInstanceAttributeHandle.hVec4()
    val shaderSampler = ShaderUniformHandle.img2D()
    val testShader1: ShaderSpec = ShaderSpec()
        .setName("Shader1")
        .addVertexAttribute("position", shaderPosition)
        .addVertexAttribute("ITextureCoord", shaderTextureCoord)
        .addInstanceAttribute("IColor", shaderColor)
        .addIntermediateAttribute("color", ShaderAttributeType.Vec4)
        .addIntermediateAttribute("textureCoord", ShaderAttributeType.Vec2)
        .addUniform("testSampler", shaderSampler, sampler)
        .setVertexShaderBody("gl_Position = vec4(position, 0.0, 1.0); color = IColor; textureCoord = ITextureCoord;")
        .setFragmentShaderBody("oColor = texture(testSampler, textureCoord);")

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
            ShaderVertexData(shaderPosition, arrayOf(Vec2(-0.5f, 0.5f), Vec2(0.5f, 0.5f), Vec2(-0.5f, -0.5f), Vec2(0.5f, -0.5f))),
            ShaderVertexData(shaderTextureCoord, arrayOf(Vec2(0.0f, 0.0f), Vec2(1.0f, 0.0f), Vec2(0.0f, 1.0f), Vec2(1.0f, 1.0f))),
            ShaderInstanceData(shaderColor, HVec4(Half.ONE, Half.ONE, Half.ZERO, Half.ONE))
        ))
        .setSizePercentage(SizePercentage(10.0, 10.0))
    val testButton2: Button = Button()
        .setName("Test Button 2")
        .setPosition(
            Anchor.toWindow(Coordinate.CENTER),
            Coordinate.CENTER, IntOffset(0, 0)
        )
        .setShader(testShader1, data = listOf(
            ShaderVertexData(shaderPosition, arrayOf(Vec2(0.5f, -0.5f), Vec2(1.0f, -0.5f), Vec2(1.0f,  0.5f), Vec2(0.5f,  0.5f))),
            ShaderVertexData(shaderTextureCoord, arrayOf(Vec2(0.0f, 0.0f), Vec2(0.0f, 1.0f), Vec2(1.0f, 0.0f), Vec2(1.0f, 1.0f))),
            ShaderInstanceData(shaderColor, HVec4(Half.ONE, Half.ONE, Half.ZERO, Half.ONE))
        ))
        .setSizePercentage(SizePercentage(10.0, 10.0))

    /*val testWindow2: Window = engine.createWindow()
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
        .addOnClickHandler({ println("Clicked") })*/

    engine.setStructure(
        testWindow1.setStructure(
            testUI1.setStructure(
                testButton1,
            )
        )
    )
    engine.run()

    engine.shutdown()
}
