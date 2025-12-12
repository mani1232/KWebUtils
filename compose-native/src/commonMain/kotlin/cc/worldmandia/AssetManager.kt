package cc.worldmandia

import okio.*
import okio.Path.Companion.toPath

data class AssetInfo(
    val offset: Long,
    val size: Int,
    val isCompressed: Boolean,
    val path: String
) {
    val etag: String get() = "\"${path.hashCode().toString(16)}-$size-$offset\""
}

private const val MAGIC = "KOTLIN_FRONTEND_APP!"
private const val MAGIC_SIZE = 20L
private const val FOOTER_SIZE = MAGIC_SIZE + 8L

class AssetManager {
    private val exePath by lazy { getSelfPath().toPath() }
    private val fileSystem = FileSystem.SYSTEM

    private val sharedHandle: FileHandle by lazy {
        try {
            fileSystem.openReadOnly(exePath)
        } catch (e: Exception) {
            println("❌ Critical Error: Cannot open executable for reading: ${e.message}")
            throw e
        }
    }

    private val index: Map<String, AssetInfo> by lazy {
        parseIndex()
    }

    private fun parseIndex(): Map<String, AssetInfo> {
        if (!fileSystem.exists(exePath)) return emptyMap()

        val tempIndex = mutableMapOf<String, AssetInfo>()

        try {
            val fileSize = sharedHandle.size()
            if (fileSize < FOOTER_SIZE) return emptyMap()

            val footerBuffer = Buffer()
            sharedHandle.read(fileSize - FOOTER_SIZE, footerBuffer, FOOTER_SIZE)

            val startOffset = footerBuffer.readLongLe()
            val magic = footerBuffer.readUtf8(MAGIC_SIZE)

            if (magic != MAGIC) {
                println("⚠️ AssetManager: Magic mismatch. Expected '$MAGIC', got '$magic'")
                return emptyMap()
            }

            val dataEnd = fileSize - FOOTER_SIZE

            sharedHandle.source(startOffset).buffer().use { source ->
                var currentPos = startOffset
                while (currentPos < dataEnd && !source.exhausted()) {
                    val pathLen = source.readIntLe()
                    val path = source.readUtf8(pathLen.toLong())
                    val isCompressed = source.readByte().toInt() == 1
                    val contentLen = source.readIntLe()

                    val headerSize = 4L + pathLen + 1 + 4
                    val contentOffset = currentPos + headerSize

                    tempIndex[path] = AssetInfo(contentOffset, contentLen, isCompressed, path)

                    source.skip(contentLen.toLong())
                    currentPos += headerSize + contentLen
                }
            }
            println("✅ AssetManager: loaded ${tempIndex.size} assets.")
        } catch (e: Exception) {
            println("❌ AssetManager Error: ${e.message}")
            e.printStackTrace()
        }
        return tempIndex
    }

    fun getAssetInfo(path: String): AssetInfo? = index[path]

    fun getSource(info: AssetInfo): Source? {
        return try {
            sharedHandle.source(info.offset).limit(info.size.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun close() {
        try {
            sharedHandle.close()
        } catch (_: Exception) {
        }
    }
}

val assetManager = AssetManager()
expect fun getSelfPath(): String

fun Source.limit(byteCount: Long): Source {
    require(byteCount >= 0) { "byteCount < 0: $byteCount" }
    return object : ForwardingSource(this) {
        private var remaining = byteCount
        override fun read(sink: Buffer, byteCount: Long): Long {
            if (remaining == 0L) return -1L
            val toRead = minOf(byteCount, remaining)
            val read = super.read(sink, toRead)
            if (read == -1L) return -1L
            remaining -= read
            return read
        }
    }
}