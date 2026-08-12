package com.aston728.engine.core

import com.aston728.engine.core.utils.Handle
import kotlin.reflect.KClass

public interface EventSubscriber {
    public fun <T : Event> subscribe(event: KClass<T>, handler: (T) -> Unit, handle: Handle? = null): EventSubscriber
}
