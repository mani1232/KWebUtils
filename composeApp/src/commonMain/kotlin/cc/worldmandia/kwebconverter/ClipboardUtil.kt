package cc.worldmandia.kwebconverter

import androidx.compose.ui.platform.Clipboard

expect suspend fun Clipboard.setPlainText(content: String)