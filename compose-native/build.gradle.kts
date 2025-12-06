import java.io.ByteArrayOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.GZIPOutputStream

plugins {
    alias(custom.plugins.kotlinMultiplatform)
    alias(custom.plugins.kotlinSerialization)
    alias(custom.plugins.ksp)
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "cc.worldmandia.main"
            }
        }
    }

    sourceSets {
        nativeMain {
            kotlin.srcDir(layout.buildDirectory.dir("generated/kotlin/assets"))
            dependencies {
                implementation(custom.webviewko)
                implementation(custom.kotlin.coroutines)
                implementation(custom.ktor.server.core)
                implementation(custom.ktor.server.cio)
            }
        }
    }
}

val packageDistribution by tasks.registering {
    group = "distribution"
    description = "Appends assets to the end of the EXE file"

    dependsOn("linkReleaseExecutableNative", ":publishKWebUtils")

    doLast {
        val buildDir = rootProject.layout.buildDirectory
        val originalExe = layout.buildDirectory.file("bin/native/releaseExecutable/compose-native.exe").get().asFile
        val wwwDir = buildDir.dir("www").get().asFile
        val outputExe = buildDir.file("compose-native-final.exe").get().asFile

        println("📦 Packaging: ${originalExe.name} + www folder -> ${outputExe.name}")

        originalExe.copyTo(outputExe, overwrite = true)

        val raf = RandomAccessFile(outputExe, "rw")
        val startOffset = raf.length()
        raf.seek(startOffset)

        println("📍 Data starts at offset: $startOffset")

        val numBuffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)

        fun writeIntLE(value: Int) {
            numBuffer.clear()
            numBuffer.putInt(value)
            raf.write(numBuffer.array(), 0, 4)
        }

        fun writeLongLE(value: Long) {
            numBuffer.clear()
            numBuffer.putLong(value)
            raf.write(numBuffer.array(), 0, 8)
        }

        var filesCount = 0

        wwwDir.walkTopDown().filter { it.isFile }.forEach { file ->
            val relativePath = "/" + file.toRelativeString(wwwDir).replace("\\", "/")

            val ext = relativePath.substringAfterLast('.', "").lowercase()
            val extensionsToGzip = setOf("html", "js", "mjs", "css", "json", "xml", "txt", "map", "wasm", "obj", "vert", "frag", "glsl", "svg")
            val shouldGzip = extensionsToGzip.contains(ext)

            val originalBytes = file.readBytes()
            val bytesToWrite = if (shouldGzip) {
                val bos = ByteArrayOutputStream()
                GZIPOutputStream(bos).use { it.write(originalBytes) }
                bos.toByteArray()
            } else {
                originalBytes
            }

            val pathBytes = relativePath.toByteArray(Charsets.UTF_8)
            writeIntLE(pathBytes.size)

            raf.write(pathBytes)

            raf.write(if (shouldGzip) 1 else 0)

            writeIntLE(bytesToWrite.size)

            raf.write(bytesToWrite)

            filesCount++
        }

        writeLongLE(startOffset)

        val magicString = "KOTLIN_FRONTEND_APP!"
        val magicBytes = magicString.toByteArray(Charsets.UTF_8)
        raf.write(magicBytes)

        raf.close()

        println("✅ PACKAGING DONE. Total files: $filesCount")
        println("📝 Footer info: Offset=$startOffset, Magic='$magicString' (${magicBytes.size} bytes)")
    }
}