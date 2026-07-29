plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.search.api"
}

dependencies {
    arrayOf(
        project(":core:database:impl"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }
}