plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.detail.api"

    testFixtures {
        enable = true
    }
}

dependencies {
    arrayOf(
        project(":core:model"),
        project(":core:userdata:api"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    testFixturesImplementation(libs.kotlinx.coroutines.core)
    testFixturesImplementation(libs.androidx.compose.paging)
    testFixturesImplementation(project(":core:model"))
    testFixturesImplementation(project(":core:database:impl"))
    testFixturesImplementation(libs.androidx.paging.testing)
}