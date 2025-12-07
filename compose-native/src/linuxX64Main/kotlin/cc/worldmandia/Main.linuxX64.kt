package cc.worldmandia

import com.github.winterreisender.webviewko.WebviewKo

actual fun startWebview(url: String) {
    val webview = WebviewKo()
    webview.title("Compose Web like Native")
    webview.size(800, 800)

    webview.url(url)

    webview.show()
}