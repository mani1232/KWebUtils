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
    //macosX64 { // TODO temporary disabled
    //    binaries {
    //        executable {
    //            entryPoint = "cc.worldmandia.main"
    //        }
    //    }
    //}
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
                //linkerOpts("-mwindows")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(custom.kotlin.coroutines)
            implementation(custom.kotlin.io.okio)
            implementation(custom.ktor.server.core)
            implementation(custom.ktor.server.cio)
            implementation(custom.webviewko)
        }
    }
}



data class TargetPlatform(
    val taskNameSuffix: String,
    val linkTaskName: String,
    val sourceBinaryPath: String,
    val outputFileName: String
)

val targetPlatforms = listOf(
    System.getProperty("os.name").lowercase().let {
        when {
            it.contains("win") -> {
                TargetPlatform(
                    taskNameSuffix = "Windows",
                    linkTaskName = "linkReleaseExecutableMingwX64",
                    sourceBinaryPath = "bin/mingwX64/releaseExecutable/compose-native.exe",
                    outputFileName = "compose-windows-final.exe"
                )
            }

            it.contains("mac") -> {
                TargetPlatform(
                    "Macos",
                    "linkReleaseExecutableMacosX64",
                    "bin/macosX64/releaseExecutable/compose-native.kexe",
                    "compose-macos-final"
                )
            }

            else -> {
                TargetPlatform(
                    "Linux",
                    "linkReleaseExecutableLinuxX64",
                    "bin/linuxX64/releaseExecutable/compose-native.kexe",
                    "compose-linux-final"
                )
            }
        }
    }
)

val MAGIC_STRING = "KOTLIN_FRONTEND_APP!"
val EXTENSIONS_TO_COMPRESS = setOf("html", "js", "mjs", "css", "json", "xml", "txt", "map", "wasm", "obj", "vert", "frag", "glsl", "svg")

val packageTasks = targetPlatforms.map { platform ->
    tasks.register("packageDistribution${platform.taskNameSuffix}") {
        group = "distribution"
        description = "Packages assets for ${platform.taskNameSuffix}"

        dependsOn(platform.linkTaskName, ":publishKWebUtils")

        val buildDir = layout.buildDirectory
        val wwwDir = rootProject.layout.buildDirectory.dir("www")
        val sourceExeFile = buildDir.file(platform.sourceBinaryPath)
        val outputExeFile = buildDir.file(platform.outputFileName)

        inputs.dir(wwwDir).withPropertyName("wwwDir")
        inputs.file(sourceExeFile).withPropertyName("sourceExe")
        outputs.file(outputExeFile).withPropertyName("outputExe")

        doLast {
            val webDirFile = wwwDir.get().asFile
            if (!webDirFile.exists()) {
                throw GradleException("WWW directory not found. Run publishKWebUtils task first: ${webDirFile.absolutePath}")
            }

            Brotli4jLoader.ensureAvailability()
            val brotliParams = Encoder.Parameters().setQuality(11)

            val sourceFile = sourceExeFile.get().asFile
            val targetFile = outputExeFile.get().asFile

            if (!sourceFile.exists()) throw GradleException("Source binary not found: ${sourceFile.absolutePath}")

            println("📦 Packaging [${platform.taskNameSuffix}] -> ${targetFile.name}")

            sourceFile.copyTo(targetFile, overwrite = true)
            targetFile.setExecutable(true)

            RandomAccessFile(targetFile, "rw").use { raf ->
                val startOffset = raf.length()
                raf.seek(startOffset)

                val leBuffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)

                fun writeInt(value: Int) {
                    leBuffer.clear()
                    leBuffer.putInt(value)
                    raf.write(leBuffer.array(), 0, 4)
                }

                fun writeLong(value: Long) {
                    leBuffer.clear()
                    leBuffer.putLong(value)
                    raf.write(leBuffer.array(), 0, 8)
                }

                var filesCount = 0
                var savedBytesTotal = 0L

                webDirFile.walkTopDown().filter { it.isFile }.forEach { file ->
                    val relativePath = "/" + file.toRelativeString(webDirFile).replace("\\", "/")
                    val ext = file.extension.lowercase()
                    val shouldCompress = ext in EXTENSIONS_TO_COMPRESS
                    val originalBytes = file.readBytes()

                    val (finalBytes, isCompressed) = if (shouldCompress) {
                        val bos = ByteArrayOutputStream(originalBytes.size / 2)
                        BrotliOutputStream(bos, brotliParams).use { it.write(originalBytes) }
                        val compressed = bos.toByteArray()
                        if (compressed.size < originalBytes.size) {
                            savedBytesTotal += (originalBytes.size - compressed.size)
                            compressed to true
                        } else originalBytes to false
                    } else originalBytes to false

                    val pathBytes = relativePath.toByteArray(Charsets.UTF_8)
                    writeInt(pathBytes.size)
                    raf.write(pathBytes)
                    raf.write(if (isCompressed) 1 else 0)
                    writeInt(finalBytes.size)
                    raf.write(finalBytes)

                    filesCount++
                }

                writeLong(startOffset)
                raf.write(MAGIC_STRING.toByteArray(Charsets.UTF_8))

                println("✅ Packed $filesCount files. Saved: ${savedBytesTotal / 1024} KB.")
            }
        }
    }
}

tasks.register("packageDistribution") {
    group = "distribution"
    description = "Packages assets for all targets"
    dependsOn(packageTasks)
}