package com.aston728.engine.core.utils

import com.aston728.engine.core.types.Handler

public class Handle(private var onRemove: Handler = {}) {
    public fun setOnRemoveHandler(handler: Handler): Handle = apply {
        this.onRemove = handler
    }

    public fun remove(): Unit {
        this.onRemove()
        this.onRemove = {}
    }
}
