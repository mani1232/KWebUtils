import com.aayushatharva.brotli4j.Brotli4jLoader
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream
import com.aayushatharva.brotli4j.encoder.Encoder
import java.io.ByteArrayOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

plugins {
    alias(custom.plugins.kotlinMultiplatform)
    alias(custom.plugins.kotlinSerialization)
    alias(custom.plugins.ksp)
}

kotlin {
    //macosArm64()
    macosX64 {
        binaries {
            executable {
                entryPoint = "cc.worldmandia.main"
            }
        }
    }
    //linuxArm64()
    linuxX64 {
        binaries {
            executable {
                entryPoint = "cc.worldmandia.main"
            }
        }
    }
    mingwX64 {
        binaries {
            executable {
                entryPoint = "cc.worldmandia.main"
                linkerOpts("-mwindows")
            }
        }
    }

    sourceSets {
        mingwX64Main.dependencies {
            implementation(custom.webviewko.windows)
        }
        linuxX64Main.dependencies {
            implementation(custom.webviewko.linux)
        }
        macosX64Main.dependencies {
            implementation(custom.webviewko.macos)
        }
        commonMain.dependencies {
            implementation(custom.kotlin.coroutines)
            implementation(custom.kotlin.io)
            implementation(custom.ktor.server.core)
            implementation(custom.ktor.server.cio)
        }
    }
}

val packageDistribution by tasks.registering {
    group = "distribution"
    description = "Appends assets to the executable (Cross-platform)"

    val platforms = listOf(
        Triple(
            "linkReleaseExecutableMingwX64",
            "bin/mingwX64/releaseExecutable/compose-native.exe",
            "compose-windows-final.exe"
        ),
        //Triple(
        //    "linkReleaseExecutableLinuxX64",
        //    "bin/linuxX64/releaseExecutable/compose-native.kexe",
        //    "compose-linux-final"
        //),
        //Triple(
        //    "linkReleaseExecutableMacosX64",
        //    "bin/macosX64/releaseExecutable/compose-native.kexe",
        //    "compose-macos-final"
        //)
    )

    dependsOn(*platforms.map { (task, binaryPath, outputName) -> task }.toTypedArray(), ":publishKWebUtils")

    doLast {
        Brotli4jLoader.ensureAvailability()
        val params = Encoder.Parameters().setQuality(11)

        platforms.forEach { (task, binaryPath, outputName) ->
            val buildDir = rootProject.layout.buildDirectory
            val originalExe = layout.buildDirectory.file(binaryPath).get().asFile
            val wwwDir = buildDir.dir("www").get().asFile
            val outputExe = buildDir.file(outputName).get().asFile
            outputExe.delete()

            println("📦 Packaging for [${outputName}]: ${originalExe.name} -> ${outputExe.name}")

            originalExe.copyTo(outputExe, overwrite = true)

            outputExe.setExecutable(true)
            println("🔧 Executable permission set (+x)")

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
                val shouldCompress = extensionsToGzip.contains(ext)

                val originalBytes = file.readBytes()
                val bytesToWrite = if (shouldCompress) {
                    val bos = ByteArrayOutputStream()
                    BrotliOutputStream(bos, params).use { it.write(originalBytes) }
                    bos.toByteArray().also {
                        val diff = originalBytes.size - it.size
                        val percent = if (originalBytes.isNotEmpty()) (diff.toDouble() / originalBytes.size * 100).toInt() else 0
                        println("📉 $relativePath: ${originalBytes.size} B -> ${it.size} B (Saved: $diff B, ~$percent%)")
                    }
                } else {
                    originalBytes
                }

                val pathBytes = relativePath.toByteArray(Charsets.UTF_8)
                writeIntLE(pathBytes.size)
                raf.write(pathBytes)
                raf.write(if (shouldCompress) 1 else 0)
                writeIntLE(bytesToWrite.size)
                raf.write(bytesToWrite)

                filesCount++
            }

            writeLongLE(startOffset)
            val magicString = "KOTLIN_FRONTEND_APP!"
            raf.write(magicString.toByteArray(Charsets.UTF_8))
            raf.close()

            println("✅ SUCCESS. Run with: ${outputExe.absolutePath}")
            println("📝 Footer info: Offset=$startOffset, Magic='$magicString' (${magicString.toByteArray(Charsets.UTF_8).size} bytes)")
        }
    }
}