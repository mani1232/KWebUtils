package cc.worldmandia

import kotlinx.io.*
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

data class AssetInfo(
    val offset: Long, val size: Int, val isCompressed: Boolean
)

class AssetManager {
    private val index = mutableMapOf<String, AssetInfo>()

    private val exePath = Path(getSelfPath())
    private val fileSystem = SystemFileSystem

    private val MAGIC = "KOTLIN_FRONTEND_APP!"
    private val MAGIC_SIZE = 20L
    private val OFFSET_SIZE = 8L
    private val FOOTER_SIZE = MAGIC_SIZE + OFFSET_SIZE

    init {
        parseIndex()
    }

    private fun parseIndex() {
        if (!fileSystem.exists(exePath)) {
            println("Warning: Could not open executable at $exePath")
            return
        }

        val metadata = try {
            fileSystem.metadataOrNull(exePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        val fileSize = metadata?.size ?: return

        if (fileSize < FOOTER_SIZE) return

        try {
            fileSystem.source(exePath).buffered().use { source ->
                source.skip(fileSize - MAGIC_SIZE)
                val magic = source.readString(MAGIC_SIZE)
                if (magic != MAGIC) return
            }

            var startOffset: Long = 0
            fileSystem.source(exePath).buffered().use { source ->
                source.skip(fileSize - FOOTER_SIZE)
                startOffset = source.readLongLe()
            }

            val dataEnd = fileSize - FOOTER_SIZE
            var currentPos = startOffset

            fileSystem.source(exePath).buffered().use { source ->
                source.skip(startOffset)

                while (currentPos < dataEnd && !source.exhausted()) {
                    val pathLen = source.readIntLe()
                    currentPos += 4

                    val path = source.readString(pathLen.toLong())
                    currentPos += pathLen

                    val isCompressedByte = source.readByte()
                    currentPos += 1
                    val isCompressed = isCompressedByte.toInt() == 1

                    val contentLen = source.readIntLe()
                    currentPos += 4

                    val contentOffset = currentPos

                    index[path] = AssetInfo(contentOffset, contentLen, isCompressed)

                    source.skip(contentLen.toLong())
                    currentPos += contentLen
                }
            }
        } catch (e: Exception) {
            println("Error parsing asset index: ${e.message}")
        }
    }

    fun load(path: String): Pair<ByteArray, Boolean>? {
        val info = index[path] ?: return null
        if (!fileSystem.exists(exePath)) return null

        return try {
            fileSystem.source(exePath).buffered().use { source ->
                source.skip(info.offset)

                val buffer = source.readByteArray(info.size)
                Pair(buffer, info.isCompressed)
            }
        } catch (e: Exception) {
            println("Error loading asset $path: ${e.message}")
            null
        }
    }

    fun exists(path: String) = index.containsKey(path)
}

val assetManager = AssetManager()

expect fun getSelfPath(): String