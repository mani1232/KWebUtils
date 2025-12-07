package cc.worldmandia

import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

fun main() {
    val server = embeddedServer(CIO, port = 0) {
        routing {
            get("/{path...}") {
                val pathSegments = call.parameters.getAll("path") ?: emptyList()
                val rawPath = "/" + pathSegments.joinToString("/")

                var requestPath = if (rawPath == "/") "/index.html" else rawPath

                if (!assetManager.exists(requestPath)) {
                    val potentialIndex =
                        if (requestPath.endsWith("/")) "${requestPath}index.html" else "$requestPath/index.html"
                    if (assetManager.exists(potentialIndex)) {
                        requestPath = potentialIndex
                    }
                }

                val asset = assetManager.load(requestPath)

                if (asset != null) {
                    val (bytes, isCompressed) = asset

                    val contentType = getContentType(requestPath)
                    if (isCompressed) {
                        call.response.header(HttpHeaders.ContentEncoding, "br")
                    }
                    call.respondBytes(bytes, contentType)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Asset not found in EXE payload")
                }
            }
        }
    }.start(wait = false)

    val port = runBlocking {
        server.engine.resolvedConnectors().first().port
    }

    startWebview("http://localhost:$port/index.html")

    server.stop(100, 100)
}

expect fun startWebview(url: String)

fun getContentType(path: String): ContentType {
    return when {
        path.endsWith(".html") -> ContentType.Text.Html
        path.endsWith(".js") -> ContentType.Application.JavaScript
        path.endsWith(".mjs") -> ContentType.Application.JavaScript
        path.endsWith(".wasm") -> ContentType.parse("application/wasm")
        path.endsWith(".css") -> ContentType.Text.CSS
        path.endsWith(".png") -> ContentType.Image.PNG
        path.endsWith(".svg") -> ContentType.Image.SVG
        path.endsWith(".json") -> ContentType.Application.Json
        else -> ContentType.Application.OctetStream
    }
}