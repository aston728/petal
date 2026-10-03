package com.aston728.engine.core.internals

import com.aston728.engine.core.geometry.IntSize
import org.lwjgl.stb.STBImage.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

public class Image private constructor(private val width: Int, private val height: Int, private val pixels: ByteBuffer) {
    public companion object {
        public val DEFAULT_IMG: Image = Image(
            2, 2,
            ByteBuffer
                .allocateDirect(16)
                .order(ByteOrder.nativeOrder())
                .put(byteArrayOf(
                    0.toByte(), 0.toByte(), 0.toByte(), 255.toByte(),
                    248.toByte(), 0.toByte(), 248.toByte(), 255.toByte(),
                    248.toByte(), 0.toByte(), 248.toByte(), 255.toByte(),
                    0.toByte(), 0.toByte(), 0.toByte(), 255.toByte(),
                ))
                .rewind()
        )
        internal fun load(path: String, errorCallback: (String) -> Unit): Image {
            val bytes: ByteArray? = Image::class.java.getResourceAsStream("/$path")?.use { it.readBytes() }
            if (bytes == null) {
                errorCallback("Failed to open image '$path'")
                return Image.DEFAULT_IMG
            }

            val buffer: ByteBuffer = ByteBuffer.allocateDirect(bytes.size).order(ByteOrder.nativeOrder()).put(bytes).rewind()
            val width: IntArray = IntArray(1)
            val height: IntArray = IntArray(1)
            val pixels: ByteBuffer? = stbi_load_from_memory(buffer, width, height, IntArray(1), STBI_rgb_alpha)

            if (pixels == null) {
                errorCallback("Failed to load image '$path': ${stbi_failure_reason()}")
                return Image.DEFAULT_IMG
            }

            val img: Image = Image(
                width[0], height[0],
                ByteBuffer.allocateDirect(pixels.limit()).order(ByteOrder.nativeOrder()).put(pixels).rewind()
            )
            pixels.rewind()
            stbi_image_free(pixels)
            return img
        }
    }

    public fun getWidth(): Int = this.width
    public fun getHeight(): Int = this.height
    public fun getPixels(): ByteBuffer = this.pixels.asReadOnlyBuffer()

    public fun scaledTo(size: IntSize): Image = this.copy()

    public fun copy(): Image {
        val img: Image = Image(
            this.width, this.height,
            ByteBuffer.allocateDirect(this.pixels.limit()).order(ByteOrder.nativeOrder()).put(this.pixels).rewind()
        )
        this.pixels.rewind()
        return img
    }
}
