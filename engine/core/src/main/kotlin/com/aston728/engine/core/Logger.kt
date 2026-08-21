package com.aston728.engine.core

public class LoggerFatalError : RuntimeException()
public class Logger : FrozenLogger {
    private var shouldShowInfo: Boolean = true
    private var shouldShowWarnings: Boolean = true
    private var shouldShowErrors: Boolean = true

    private var format: (String, String, String) -> String = { severity: String, tag: String, message: String -> "[$tag] $severity: $message" }
    private var log: (String) -> Unit = { message -> System.err.println(message) }

    override fun areInfoEnabled(): Boolean = this.shouldShowInfo
    override fun areWarningsEnabled(): Boolean = this.shouldShowWarnings
    override fun areErrorsEnabled(): Boolean = this.shouldShowErrors

    public fun setShowInfo(shouldShowInfo: Boolean): Logger = apply {
        this.shouldShowInfo = shouldShowInfo
    }
    public fun setShowWarnings(shouldShowWarnings: Boolean): Logger = apply {
        this.shouldShowWarnings = shouldShowWarnings
    }
    public fun setShowErrors(shouldShowErrors: Boolean): Logger = apply {
        this.shouldShowErrors = shouldShowErrors
    }
    public fun setFormat(format: (String, String, String) -> String): Logger = apply {
        this.format = format
    }
    public fun setHandler(handler: (String) -> Unit): Logger = apply {
        this.log = handler
    }

    override fun info(tag: String, message: String): Logger = apply {
        if (this.shouldShowInfo) { this.log(this.format("INFO", tag, message)) }
    }
    override fun warn(tag: String, message: String): Logger = apply {
        if (this.shouldShowWarnings) { this.log(this.format("WARNING", tag, message)) }
    }
    override fun error(tag: String, message: String): Logger = apply {
        if (this.shouldShowErrors) { this.log(this.format("ERROR", tag, message)) }
    }
    override fun fatalError(tag: String, message: String): Nothing {
        this.log(this.format("FATAL ERROR", tag, message))
        throw LoggerFatalError()
    }
}
