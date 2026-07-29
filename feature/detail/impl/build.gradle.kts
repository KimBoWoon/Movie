plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.detail.impl"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":feature:favorite:api"),
        project(":core:userdata:api"),
        project(":core:model"),
        project(":core:notifications:api"),
        project(":core:firebase"),
        project(":core:analytics:api"),
        project(":core:database:impl"),
        project(":core:network"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.media3.exoplayer,
        libs.androidx.media3.ui,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    api(project(":feature:detail:api"))

    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.androidx.paging.common)
    testImplementation(libs.turbine)

    androidTestImplementation(project(":core:testing"))
}