package cc.worldmandia

import platform.Foundation.NSBundle

actual fun getSelfPath(): String {
    return NSBundle.mainBundle.executablePath
        ?: throw IllegalStateException("Could not determine executable path from NSBundle")
}