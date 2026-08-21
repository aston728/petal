package com.aston728.engine.core.utils

public class Handle(private var onRemove: () -> Unit = {}) {
    public fun setOnRemoveHandler(handler: () -> Unit): Handle = apply {
        this.onRemove = handler
    }

    public fun remove(): Unit {
        this.onRemove()
        this.onRemove = {}
    }
}
