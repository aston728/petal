package com.aston728.engine.core

interface FrozenLogger {
    fun info(tag: String, message: String): FrozenLogger
    fun warn(tag: String, message: String): FrozenLogger
    fun error(tag: String, message: String): FrozenLogger
    fun fatalError(tag: String, message: String): Nothing
}
