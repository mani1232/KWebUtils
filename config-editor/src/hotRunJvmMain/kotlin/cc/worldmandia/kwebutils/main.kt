package cc.worldmandia.kwebutils

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cc.worldmandia.kwebutils.presentation.feature.dashboard.FileUploadCard

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        alwaysOnTop = true,
        title = "Config Editor",
    ) {
        StartConfigEditorApp()
    }
}

@Composable
@Preview
fun TestPreview() {
    FileUploadCard()
}