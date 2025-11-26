plugins {
    alias(libs.plugins.kotlinMultiplatform)
    //alias(libs.plugins.composeMultiplatform)
    //alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    //alias(libs.plugins.ksp)
    //alias(libs.plugins.composePwa)
}

kotlin {
    js {
        browser {
            commonWebpackConfig {
                outputFileName = "index-menu.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(kotlinWrappers.browser)
            implementation(kotlinWrappers.web)

            implementation(kotlinWrappers.react)
            implementation(kotlinWrappers.reactDom)
            implementation(kotlinWrappers.reactRouter)

            implementation(kotlinWrappers.emotion.styled)

            implementation(kotlinWrappers.mui.material)
            implementation(kotlinWrappers.mui.iconsMaterial)
            implementation(kotlinWrappers.mui.system)
        }
        webMain.dependencies {
            //implementation(libs.compose.runtime)
        }
        commonMain.dependencies {
            //implementation(libs.compose.html)
        }
    }
}