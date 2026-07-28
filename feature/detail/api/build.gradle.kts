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
        project(":core:common"),
        project(":feature:favorite:api"),
        project(":core:model"),
        project(":core:notifications:api"),
        project(":core:firebase"),
        project(":core:analytics:api"),
        project(":core:userdata:api"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.media3.exoplayer,
        libs.androidx.media3.ui,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.androidx.paging.common)
    testImplementation(libs.turbine)

    androidTestImplementation(project(":core:testing"))
}