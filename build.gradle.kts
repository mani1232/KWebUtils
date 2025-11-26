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

        println("📂 Сборка сайта в: ${wwwDir.absolutePath}")

        val menuDist = project(":$menuApp").layout.buildDirectory
            .dir("dist/js/productionExecutable").get().asFile

        if (menuDist.exists()) {
            copy {
                from(menuDist)
                into(wwwDir)
            }
            println("✅ Меню (index.html) скопировано в корень.")
        } else {
            error("❌ Не найдена сборка меню! Проверьте путь: ${menuDist.path}")
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
                println("✅ Приложение $appName скопировано в /$appName")
            } else {
                println("⚠️ Сборка для $appName не найдена.")
            }
        }

        println("🏁 Готово! Запустите сервер в папке build/www")
    }
}