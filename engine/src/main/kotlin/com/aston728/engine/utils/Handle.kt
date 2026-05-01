package com.aston728.engine.utils

import com.aston728.engine.types.Handler

class Handle(private var onRemoveHandler: Handler = {}) {
    fun setOnRemoveHandler(handler: Handler): Handle = apply {
        this.onRemoveHandler = handler
    }

    fun remove(): Unit { this.onRemoveHandler() }
}
