package cc.worldmandia

import kotlinx.cinterop.*
import platform.posix.*
import platform.windows.GetModuleFileNameA
import platform.windows.MAX_PATH

data class AssetInfo(
    val offset: Int,
    val size: Int,
    val isGzipped: Boolean
)
@OptIn(ExperimentalForeignApi::class)
class AssetManager {
    private val index = mutableMapOf<String, AssetInfo>()
    private val exePath: String
    private val MAGIC = "KOTLIN_FRONTEND_APP!"
    private val MAGIC_SIZE = 20
    private val OFFSET_SIZE = 8
    private val FOOTER_SIZE = MAGIC_SIZE + OFFSET_SIZE

    init {
        exePath = getSelfPath()
        println("🔎 Self Path: $exePath")
        parseIndex()
    }

    private fun getSelfPath(): String = memScoped {
        val buffer = allocArray<ByteVar>(MAX_PATH)
        GetModuleFileNameA(null, buffer, MAX_PATH.toUInt())
        return buffer.toKString()
    }

    private fun parseIndex() {
        memScoped {
            val file = fopen(exePath, "rb") ?: error("Cannot open self exe!")

            try {
                fseek(file, -MAGIC_SIZE, SEEK_END)

                val magicBuf = allocArray<ByteVar>(MAGIC_SIZE + 1)
                fread(magicBuf, 1u, MAGIC_SIZE.toULong(), file)
                val readMagic = magicBuf.toKString().substring(0, MAGIC_SIZE)

                if (readMagic != MAGIC) {
                    return
                }

                fseek(file, -FOOTER_SIZE, SEEK_END)

                val offsetBuf = alloc<IntVar>()
                fread(offsetBuf.ptr, OFFSET_SIZE.toULong(), 1u, file)
                val startOffset = offsetBuf.value

                fseek(file, startOffset, SEEK_SET)

                fseek(file, 0, SEEK_END)
                val fileSize = ftell(file)
                val dataEnd = fileSize - FOOTER_SIZE
                fseek(file, startOffset, SEEK_SET)

                while (ftell(file) < dataEnd) {
                    val pathLenBuf = alloc<IntVar>()
                    if (fread(pathLenBuf.ptr, 4u, 1u, file) < 1u) break
                    val pathLen = pathLenBuf.value

                    val pathBuf = allocArray<ByteVar>(pathLen + 1)
                    fread(pathBuf, 1u, pathLen.toULong(), file)
                    pathBuf[pathLen] = 0
                    val path = pathBuf.toKString()

                    val gzipBuf = alloc<ByteVar>()
                    fread(gzipBuf.ptr, 1u, 1u, file)
                    val isGzipped = gzipBuf.value.toInt() == 1

                    val contentLenBuf = alloc<IntVar>()
                    fread(contentLenBuf.ptr, 4u, 1u, file)
                    val contentLen = contentLenBuf.value

                    val contentOffset = ftell(file)
                    index[path] = AssetInfo(contentOffset, contentLen, isGzipped)

                    fseek(file, contentLen, SEEK_CUR)
                }
            } finally {
                fclose(file)
            }
        }
    }

    fun load(path: String): Pair<ByteArray, Boolean>? {
        val info = index[path] ?: return null

        memScoped {
            val file = fopen(exePath, "rb") ?: return null
            try {
                fseek(file, info.offset, SEEK_SET)

                val buffer = ByteArray(info.size)
                buffer.usePinned { pinned ->
                    fread(pinned.addressOf(0), 1u, info.size.toULong(), file)
                }

                return Pair(buffer, info.isGzipped)
            } finally {
                fclose(file)
            }
        }
    }

    fun exists(path: String) = index.containsKey(path)
}

val assetManager = AssetManager()