@file:Suppress("UnstableApiUsage")

include(":feature:statistics")


include(":feature:auth")


include(":feature:history")


include(":feature:settings")


include(":feature:puzzle")


include(":feature:home")


include(":core:ui")


include(":core:designsystem")


include(":core:data")


include(":core:domain")


include(":core:common")


pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tectonic"
include(":app")
