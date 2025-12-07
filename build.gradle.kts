import cc.worldmandia.FrontEnd.MENU_APP
import cc.worldmandia.FrontEnd.wasmApps

buildscript {
    dependencies {
        classpath("com.aayushatharva.brotli4j:brotli4j:1.20.0")
        classpath("com.aayushatharva.brotli4j:native-${
            System.getProperty("os.name").lowercase().let {
                when {
                    it.contains("win") -> "windows-x86_64"
                    it.contains("mac") -> "osx-x86_64"
                    else -> {
                        "linux-x86_64"
                    }
                }
            }
        }:1.20.0")
    }
}

plugins {
    alias(custom.plugins.composeMultiplatform) apply false
    alias(custom.plugins.composeCompiler) apply false
    alias(custom.plugins.kotlinMultiplatform) apply false
    alias(custom.plugins.kotlinSerialization) apply false
    alias(custom.plugins.composePwa) apply false
    alias(custom.plugins.androidMultiplatform) apply false
    alias(custom.plugins.androidApplication) apply false
    alias(custom.plugins.ksp) apply false
    alias(custom.plugins.ktor) apply false
    alias(custom.plugins.kotlinJvm) apply false
    alias(custom.plugins.composeHotReload) apply false
    alias(custom.plugins.rpc) apply false
}

tasks.register("publishKWebUtils") {
    group = "distribution"
    description = "Collects Menu in the root and Apps in subfolders"

    dependsOn(":${MENU_APP}:jsBrowserDistribution")
    dependsOn(wasmApps.map { ":$it:wasmJsBrowserDistribution" })

    doLast {
        val wwwDir = rootProject.layout.buildDirectory.dir("www").get().asFile
        if (wwwDir.exists()) wwwDir.deleteRecursively()
        wwwDir.mkdirs()

        println("📂 Assembling site in: ${wwwDir.absolutePath}")

        val menuDist = project(":$MENU_APP").layout.buildDirectory
            .dir("dist/js/productionExecutable").get().asFile

        if (menuDist.exists()) {
            copy {
                from(menuDist)
                into(wwwDir)
            }
            println("✅ Menu (index.html) copied to root.")
        } else {
            error("❌ Menu build not found! Check path: ${menuDist.path}")
        }

        wasmApps.forEach { appName ->
            val appDist = project(":$appName").layout.buildDirectory
                .dir("dist/wasmJs/productionExecutable").get().asFile

            if (appDist.exists()) {
                val targetDir = File(wwwDir, appName)
                targetDir.mkdirs()
                copy {
                    from(appDist)
                    into(targetDir)
                }
                println("✅ App $appName copied to /$appName")
            } else {
                println("⚠️ Build for $appName not found.")
            }
        }

        println("🏁 Done! Start server in build/www folder")
    }
}