plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.detail.api"
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
}