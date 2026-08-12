package com.aston728.engine.core

public interface FrozenLogger {
    public fun areInfoEnabled(): Boolean
    public fun areWarningsEnabled(): Boolean
    public fun areErrorsEnabled(): Boolean
    public fun info(tag: String, message: String): FrozenLogger
    public fun warn(tag: String, message: String): FrozenLogger
    public fun error(tag: String, message: String): FrozenLogger
    public fun fatalError(tag: String, message: String): Nothing
}
