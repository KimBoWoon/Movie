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
include(":core:ui")
include(":core:model")
include(":core:firebase")
include(":core:testing")
include(":benchmark")
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
include(":core:sync:api")
include(":core:sync:impl")
include(":core:analytics:api")
include(":core:analytics:impl")
include(":core:notifications:api")
include(":core:notifications:impl")
include(":core:database:api")
include(":core:database:impl")
include(":core:designsystem")
include(":core:network:api")
include(":core:network:impl")
