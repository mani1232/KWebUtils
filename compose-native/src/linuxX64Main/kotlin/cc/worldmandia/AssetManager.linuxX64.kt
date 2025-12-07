package cc.worldmandia

import kotlinx.cinterop.*
import platform.posix.PATH_MAX
import platform.posix.errno
import platform.posix.readlink
import platform.posix.realpath

@OptIn(ExperimentalForeignApi::class)
actual fun getSelfPath(): String = memScoped {
    val buffer = allocArray<ByteVar>(PATH_MAX)
    val len = readlink("/proc/self/exe", buffer, PATH_MAX.toULong())

    if (len == -1L) {
        throw IllegalStateException("Failed to readlink /proc/self/exe. Errno: $errno")
    }

    val rawPath = buffer.toKString().substring(0, len.toInt())

    val resolvedBuffer = allocArray<ByteVar>(PATH_MAX)
    val resultPtr = realpath(rawPath, resolvedBuffer)

    if (resultPtr != null) {
        return@memScoped resolvedBuffer.toKString()
    }

    return@memScoped rawPath
}