pluginManagement {
    includeBuild("build-logic")
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
    }
}

rootProject.name = "Movie"
include(":app")
include(":core:data")
include(":core:domain")
include(":core:common")
include(":core:network")
include(":core:ui")
include(":core:model")
include(":core:datastore")
include(":feature:home")
include(":feature:detail")
include(":feature:search")
include(":feature:favorite")
include(":feature:my")
include(":core:database")
include(":feature:people")
include(":core:sync")
include(":core:notifications")
include(":core:firebase")
include(":core:testing")
include(":core:datastore-test")
include(":feature:series")
