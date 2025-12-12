package cc.worldmandia

import kotlinx.cinterop.*
import platform.windows.GetLastError
import platform.windows.GetModuleFileNameW
import platform.windows.WCHARVar

@OptIn(ExperimentalForeignApi::class)
actual fun getSelfPath(): String = memScoped {
    val bufferSize = 32767
    val buffer = allocArray<WCHARVar>(bufferSize)

    val len = GetModuleFileNameW(null, buffer, bufferSize.toUInt())

    if (len == 0u) {
        throw IllegalStateException("Failed to get executable path. Error: ${GetLastError()}")
    }

    return@memScoped buffer.toKString()
}