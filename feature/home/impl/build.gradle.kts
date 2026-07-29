plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.home.impl"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:model"),
        project(":core:notifications:api"),
        project(":core:firebase"),
        project(":core:analytics:api"),
        project(":core:datamanager:api"),
        project(":feature:detail:api"),
        project(":core:network:api"),
        libs.coil.compose,
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    api(project(":feature:home:api"))

    testImplementation(libs.androidx.paging.testing)
}