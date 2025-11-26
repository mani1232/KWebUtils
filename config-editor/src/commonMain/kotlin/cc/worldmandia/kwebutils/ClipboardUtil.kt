package cc.worldmandia.kwebutils

import androidx.compose.ui.platform.Clipboard

expect suspend fun Clipboard.setPlainText(content: String)