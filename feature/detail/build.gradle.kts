plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.detail"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:data"),
        project(":core:domain"),
        project(":core:datastore"),
        project(":core:model"),
        project(":core:notifications"),
        project(":core:firebase"),
        project(":core:navigation"),
        project(":core:analytics"),
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