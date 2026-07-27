plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.home.api"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:datastore"),
        project(":core:model"),
        project(":core:notifications"),
        project(":core:firebase"),
        project(":core:analytics"),
        libs.coil.compose,
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    testImplementation(libs.androidx.paging.testing)
}