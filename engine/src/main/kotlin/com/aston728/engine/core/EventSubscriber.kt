package com.aston728.engine.core

import kotlin.reflect.KClass

import com.aston728.engine.utils.Handle

interface EventSubscriber {
    fun <T : Event> subscribe(event: KClass<T>, handler: (T) -> Unit, handle: Handle? = null): EventSubscriber
}
