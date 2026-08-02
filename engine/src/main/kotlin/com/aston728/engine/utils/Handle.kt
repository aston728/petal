package com.aston728.engine.utils

import com.aston728.engine.types.Handler

class Handle(private var onRemove: Handler = {}) {
    fun setOnRemoveHandler(handler: Handler): Handle = apply {
        this.onRemove = handler
    }

    fun remove(): Unit { this.onRemove() }
}
