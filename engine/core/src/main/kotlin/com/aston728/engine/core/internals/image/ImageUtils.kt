package com.aston728.engine.core.internals.image

import com.aston728.engine.core.geometry.IntSize
import com.aston728.engine.core.types.Img
import com.aston728.engine.core.types.Imgs

public object ImageUtils {
    public fun scale(img: Img, size: IntSize): Img = img
    public fun scale(imgs: Imgs, size: IntSize): Imgs = imgs
}
