package com.aston728.engine.core

import com.aston728.engine.core.internals.image.Image

public class AssetManager internal constructor(private val logger: FrozenLogger) {
    public fun loadImage(path: String): Image = Image.load(path, errorCallback = { message -> this.logger.error("IMAGE", message) })
}
