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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
<<<<<<< Updated upstream
        maven(url = "https://jitpack.io")
=======
        maven(url = "https://jitpack.io") // Add jitpack
>>>>>>> Stashed changes
    }
}

rootProject.name = "DowntimeGuard"
include(":app")
