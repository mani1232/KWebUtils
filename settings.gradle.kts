rootProject.name = "KWebUtils"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://central.sonatype.com/repository/maven-snapshots/")
        maven("https://packages.jetbrains.team/maven/p/firework/dev")
        maven("https://redirector.kotlinlang.org/maven/bootstrap")
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven("https://repo.worldmandia.cc/snapshots")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://packages.jetbrains.team/maven/p/firework/dev")
        maven("https://central.sonatype.com/repository/maven-snapshots/")
        maven("https://redirector.kotlinlang.org/maven/bootstrap")
        maven("https://gitlab.com/api/v4/projects/38224197/packages/maven/")
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }

    versionCatalogs {
        create("kotlinWrappers") {
            val wrappersVersion = "2026.2.11"
            from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:$wrappersVersion")
        }
        create("custom") {
            from(files("gradle/custom.versions.toml"))

            val devVersion = providers.gradleProperty("compose-dev.version").get()

            version("androidx-lifecycle", "2.10.0-alpha09$devVersion")
            version("androidx-nav3", "1.1.0-alpha03$devVersion")
            version("androidx-adaptive", "1.3.0-alpha05$devVersion")
            version("androidx-material3", "1.11.0-alpha03$devVersion")
            version("composeMultiplatform", "1.11.0-alpha03$devVersion")
        }
    }
}

include(":config-editor")
//include(":compose-example") // Only for Android or maybe for non-web targets
include(":index-menu")
include(":backend")
include("compose-native")