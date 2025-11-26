plugins {
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.composePwa) apply false
    alias(libs.plugins.androidMultiplatform) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.ksp) apply false
}

val wasmApps = listOf("config-editor")
val menuApp = "index-menu"

tasks.register("publishWebHub") {
    group = "distribution"
    description = "Collects Menu in the root and Apps in subfolders"

    dependsOn(":$menuApp:jsBrowserDistribution")
    dependsOn(wasmApps.map { ":$it:wasmJsBrowserDistribution" })

    doLast {
        val wwwDir = layout.buildDirectory.dir("www").get().asFile
        if (wwwDir.exists()) wwwDir.deleteRecursively()
        wwwDir.mkdirs()

        println("📂 Assembling site in: ${wwwDir.absolutePath}")

        val menuDist = project(":$menuApp").layout.buildDirectory
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