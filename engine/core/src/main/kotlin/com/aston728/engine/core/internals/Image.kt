package com.aston728.engine.core.internals

import com.aston728.engine.core.AssetLoader
import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.utils.Color
import com.aston728.engine.core.utils.Handle
import org.lwjgl.stb.STBImage.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path
import java.nio.file.Paths

public class Image private constructor(private var width: Int, private var height: Int, private var pixels: ByteBuffer) {
    private val onChangeHandlers: MutableList<(Image) -> Unit> = mutableListOf()

    public companion object {
        public var DEFAULT_IMG: Image = Image(
            2, 2,
            ByteBuffer
                .allocateDirect(16)
                .order(ByteOrder.nativeOrder()) // TODO
                .put(byteArrayOf(
                    0.toByte(), 0.toByte(), 0.toByte(), 255.toByte(),
                    248.toByte(), 0.toByte(), 248.toByte(), 255.toByte(),
                    248.toByte(), 0.toByte(), 248.toByte(), 255.toByte(),
                    0.toByte(), 0.toByte(), 0.toByte(), 255.toByte(),
                ))
                .rewind()
        )
    }

    public constructor(width: Int = 0, height: Int = 0) : this(
        width, height,
        ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder())
    )
    public constructor(vararg pathParts: String, assetLoader: AssetLoader, shouldHotReload: Boolean = false) : this(0, 0, ByteBuffer.allocateDirect(0)) {
        this.load(*pathParts, assetLoader = assetLoader, shouldHotReload = shouldHotReload)
    }

    public fun getWidth(): Int = this.width
    public fun getHeight(): Int = this.height
    public fun getPixels(): ByteBuffer = this.pixels.asReadOnlyBuffer()
    public fun getPixel(x: Int, y: Int): Color {
        val pos: Int = (y * width + x) * 4
        return Color(
            this.pixels[pos + 0].toUByte(),
            this.pixels[pos + 1].toUByte(),
            this.pixels[pos + 2].toUByte(),
            this.pixels[pos + 3].toUByte(),
        )
    }

    public fun load(vararg pathParts: String, assetLoader: AssetLoader, shouldHotReload: Boolean = false): Image = synchronized(this) { apply {
        val path: Path = Paths.get("", *pathParts)

        val bytes: ByteBuffer? =
            if (shouldHotReload) { assetLoader.getFileBytesBuffer(path) { this.load(*pathParts, assetLoader = assetLoader) } }
            else { assetLoader.getFileBytesBuffer(path) }
        if (bytes == null) {
            assetLoader.error("Failed to open image '$path'")
            return this.set(Image.DEFAULT_IMG)
        }

        val width: IntArray = IntArray(1)
        val height: IntArray = IntArray(1)
        val pixels: ByteBuffer? = stbi_load_from_memory(bytes, width, height, IntArray(1), STBI_rgb_alpha)
        if (pixels == null) {
            assetLoader.error("Failed to load image '$path': ${stbi_failure_reason()}")
            return this.set(Image.DEFAULT_IMG)
        }

        this.width = width[0]
        this.height = height[0]
        this.pixels = ByteBuffer.allocateDirect(pixels.limit()).order(ByteOrder.nativeOrder()).put(pixels).rewind()
        this.onChangeHandlers.toList().forEach { it(this) }

        pixels.rewind()
        stbi_image_free(pixels)
    } }

    public fun setPixel(x: Int, y: Int, color: Color): Image = apply {
        val pos: Int = (y * width + x) * 4
        this.pixels.put(pos + 0, color.r.toByte())
        this.pixels.put(pos + 1, color.g.toByte())
        this.pixels.put(pos + 2, color.b.toByte())
        this.pixels.put(pos + 3, color.a.toByte())
        this.onChangeHandlers.toList().forEach { it(this) }
    }
    public fun set(img: Image): Image = apply {
        this.width = img.width
        this.height = img.height
        this.pixels = ByteBuffer.allocateDirect(img.pixels.limit()).order(ByteOrder.nativeOrder()).put(img.pixels).rewind()
        img.pixels.rewind()
        this.onChangeHandlers.toList().forEach { it(this) }
    }
    public fun scaledTo(size: IntSize): Image {
        if (size.width == this.width && size.height == this.height) { return this }
        return this.copy()
    }
    public fun addOnChangeHandler(handler: (Image) -> Unit, handle: Handle? = null): Image = apply {
        val wrapper: (Image) -> Unit = { image -> handler(image) }
        this.onChangeHandlers.add(wrapper)
        handle?.setOnRemoveHandler { this.onChangeHandlers.remove(wrapper) }
    }

    override fun equals(other: Any?): Boolean =
        other is Image &&
        this.width == other.width &&
        this.height == other.height &&
        this.pixels == other.pixels
    override fun hashCode(): Int {
        var result: Int = width
        result = 31 * result + height
        result = 31 * result + pixels.hashCode()
        result = 31 * result + onChangeHandlers.hashCode()
        return result
    }
    public fun copy(): Image {
        val img: Image = Image(
            this.width, this.height,
            ByteBuffer.allocateDirect(this.pixels.limit()).order(ByteOrder.nativeOrder()).put(this.pixels).rewind()
        )
        this.pixels.rewind()
        return img
    }
}
