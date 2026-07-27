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

rootProject.name = "Surfy"
include(":surfy")
include(":core:common")
include(":core:network")
include(":core:ui")
include(":core:model")
include(":core:datastore")
include(":core:database")
include(":core:sync")
include(":core:notifications")
include(":core:firebase")
include(":core:testing")
include(":core:datastore-test")
include(":benchmark")
include(":core:analytics")
include(":feature:favorite:api")
include(":feature:favorite:impl")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:detail:api")
include(":feature:detail:impl")
include(":core:userdata:api")
include(":core:userdata:impl")
include(":core:datamanager:impl")
include(":core:datamanager:api")
