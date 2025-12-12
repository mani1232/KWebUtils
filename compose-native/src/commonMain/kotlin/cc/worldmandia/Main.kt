package cc.worldmandia

import com.github.winterreisender.webviewko.WebviewKo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.io.okio.asOkioSink
import okio.buffer
import okio.use

fun main() {
    val server = embeddedServer(CIO, port = 0) {
        configureAssetRouting()
    }.start(wait = false)

    val port = runBlocking {
        server.engine.resolvedConnectors().first().port
    }

    try {
        println("Pre-loading assets: " + assetManager.getAssetInfo("/index.html"))
    } catch (e: Exception) {
        println("Warning: Failed to preload assets: $e")
    }

    try {
        startWebview("http://localhost:$port/index.html")
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        println("Stopping server...")
        server.stop(100, 500)
        assetManager.close()
    }
}

fun Application.configureAssetRouting() {
    routing {
        route("/api") {
            get("/health") {
                call.respondText("OK")
            }
        }

        get("/{path...}") {
            val pathParams = call.parameters.getAll("path")?.joinToString("/") ?: ""
            val rawPath = "/$pathParams"

            val assetPath = resolvePath(rawPath) ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val info = assetManager.getAssetInfo(assetPath) ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            if (call.request.headers[HttpHeaders.IfNoneMatch] == info.etag) {
                call.respond(HttpStatusCode.NotModified)
                return@get
            }

            val contentType = when {
                assetPath.endsWith(".wasm") -> ContentType.parse("application/wasm")
                assetPath.endsWith(".js") || assetPath.endsWith(".mjs") -> ContentType.Application.JavaScript
                assetPath.endsWith(".css") -> ContentType.Text.CSS
                assetPath.endsWith(".html") -> ContentType.Text.Html
                else -> ContentType.defaultForFileExtension(assetPath)
            }

            call.response.header(HttpHeaders.ETag, info.etag)
            call.response.header(HttpHeaders.CacheControl, "public, max-age=31536000, immutable")

            if (info.isCompressed) {
                call.response.header(HttpHeaders.ContentEncoding, "br")
            }

            val source = withContext(Dispatchers.IO) {
                assetManager.getSource(info)
            }

            if (source != null) {
                call.respondBytesWriter(contentType) {
                    source.use { rawSource ->
                        rawSource.buffer().use { bufferedSource ->
                            bufferedSource.readAll(this.asSink().asOkioSink())
                        }
                    }
                }
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to read asset source")
            }
        }
    }
}

fun resolvePath(path: String): String? {
    val cleanPath = path.substringBefore('?').substringBefore('#')

    if (assetManager.getAssetInfo(cleanPath) != null) {
        return cleanPath
    }

    val potentialIndex = if (cleanPath.endsWith("/")) "${cleanPath}index.html" else "$cleanPath/index.html"
    if (assetManager.getAssetInfo(potentialIndex) != null) {
        return potentialIndex
    }

    val hasExtension = cleanPath.substringAfterLast('/', "").contains('.')
    if (!hasExtension && assetManager.getAssetInfo("/index.html") != null) {
        return "/index.html"
    }

    return null
}

@OptIn(ExperimentalForeignApi::class)
fun startWebview(url: String) {
    val webview = WebviewKo()
    webview.title("Compose Web like Native")
    webview.size(800, 600, WebviewKo.WindowHint.Min)
    webview.size(1280, 720, WebviewKo.WindowHint.None)
    // webview.size(1280, 720, WebviewKo.WindowHint.Max) // commented for allow Fullscreen
    webview.url(url)
    webview.show()
}