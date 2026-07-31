plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.search.api"

    testFixtures {
        enable = true
    }
}

dependencies {
    arrayOf(
        project(":core:database:impl"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    testFixturesImplementation(libs.kotlinx.coroutines.core)
    testFixturesImplementation(libs.androidx.compose.paging)
    testFixturesImplementation(libs.androidx.paging.testing)
    testFixturesImplementation(project(":core:database:impl"))
    testFixturesImplementation(project(":core:testing"))
}