plugins {
    id("bowoon.library")
    id("bowoon.hilt")
}

android {
    namespace = "com.bowoon.data"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.model,
        projects.core.network,
        projects.core.datastore,
        projects.core.database,
        libs.androidx.compose.paging,
        libs.threetenabp
    ).forEach {
        implementation(it)
    }
}