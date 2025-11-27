package cc.worldmandia.kwebutils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual suspend fun Clipboard.setPlainText(content: String) {
    setClipEntry(ClipEntry(StringSelection(content)))
}