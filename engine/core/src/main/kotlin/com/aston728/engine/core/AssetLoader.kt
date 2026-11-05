package com.aston728.engine.core

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.*
import java.util.Collections.synchronizedList
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.io.path.absolute


public class AssetLoader internal constructor(private val logger: FrozenLogger) {
    private var watchService: WatchService? = null
    private val hotReloadMappings: ConcurrentHashMap<Path, MutableList<(Path) -> Unit>> = ConcurrentHashMap()

    private fun addToHotReload(path: Path, handler: (Path) -> Unit): Unit {
        val watchService: WatchService = this.watchService ?: return

        val fullPath: Path = path.absolute().normalize()
        try {
            fullPath.parent.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
            )
            val handlers: MutableList<(Path) -> Unit> = this.hotReloadMappings.getOrPut(fullPath) { synchronizedList(mutableListOf()) }
            handlers.add(handler)
        } catch (e: IOException) {
            this.error("Failed to start hot reloading for '$fullPath': ${e.message}")
        }
    }
    private fun startHotReloading(): Unit {
        try {
            this.watchService = FileSystems.getDefault().newWatchService()
        } catch (e: IOException) {
            this.error("Failed to start hot reloading service: ${e.message}")
            return
        }

        try { thread (isDaemon = true, name = "Hot Reloading Thread") {
            while (true) {
                val key: WatchKey = this.watchService!!.take()
                val dir: Path = (key.watchable() as Path).toAbsolutePath().normalize()

                for (event in key.pollEvents()) {
                    if (event.kind() != StandardWatchEventKinds.OVERFLOW) {
                        val path: Path = dir.resolve(event.context() as Path)
                        this.hotReloadMappings[path]?.toList()?.forEach { it(path) }
                    }
                }

                if (!key.reset()) {
                    this.error("Hot reloading for '$dir' was stopped")
                    this.hotReloadMappings.keys.removeIf { it.parent == dir }
                    if (this.hotReloadMappings.isEmpty()) { break }
                }
            }
            this.watchService = null
        }} catch (_: InterruptedException) {
            this.error("Hot reloading service was interrupted")
            this.hotReloadMappings.clear()
            this.watchService = null
        }
    }

    public fun error(message: String): AssetLoader = apply {
        this.logger.error("ASSET MANAGER", message)
    }

    public fun getFileBytesArray(path: Path, hotReloadHandler: ((Path) -> Unit)? = null): ByteArray? {
        if (hotReloadHandler != null) {
            if (this.watchService == null) { this.startHotReloading() }
            this.addToHotReload(path, hotReloadHandler)
        }

        return try {
            Files.readAllBytes(path)
        } catch (_: IOException) {
            null
        }
    }
    public fun getFileBytesBuffer(path: Path, hotReloadHandler: ((Path) -> Unit)? = null): ByteBuffer? {
        if (hotReloadHandler != null) {
            if (this.watchService == null) { this.startHotReloading() }
            this.addToHotReload(path, hotReloadHandler)
        }

        var bytes: ByteBuffer? = null
        try {
            FileChannel.open(path, StandardOpenOption.READ).use {
                if (it.size() <= Int.MAX_VALUE) {
                    bytes = ByteBuffer.allocateDirect(it.size().toInt())
                    while (bytes.hasRemaining()) { it.read(bytes) }
                    bytes.flip()
                }
            }
        } catch (_: IOException) {}
        return bytes
    }
}
