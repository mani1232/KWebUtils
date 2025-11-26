package cc.worldmandia.kwebutils

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

actual suspend fun Clipboard.setPlainText(content: String) {
    setClipEntry(
        ClipEntry.withPlainText(content)
    )
}