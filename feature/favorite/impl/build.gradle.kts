plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.favorite.impl"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:model"),
        project(":core:firebase:api"),
        project(":core:analytics:api"),
        project(":core:database:impl"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    api(project(":feature:favorite:api"))

    testImplementation(libs.androidx.paging.testing)
}