package cc.worldmandia

import kotlinx.cinterop.*
import platform.windows.GetLastError
import platform.windows.GetModuleFileNameW

@OptIn(ExperimentalForeignApi::class)
actual fun getSelfPath(): String = memScoped {
    val bufferSize = 32767u
    val buffer = allocArray<UShortVar>(bufferSize.toInt())

    val len = GetModuleFileNameW(null, buffer, bufferSize)

    if (len == 0u) {
        throw IllegalStateException("Failed to get executable path. Error: ${GetLastError()}")
    }

    return@memScoped buffer.toKString()
}