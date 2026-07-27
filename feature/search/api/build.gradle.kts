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
        project(":core:common"),
        project(":core:datastore"),
        project(":core:database"),
        project(":core:model"),
        project(":core:firebase"),
        project(":core:analytics"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    testImplementation(libs.androidx.paging.testing)
    androidTestImplementation(libs.androidx.paging.testing)
}