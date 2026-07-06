plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.favorite"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:data"),
        project(":core:domain"),
        project(":core:datastore"),
        project(":core:model"),
        project(":core:firebase"),
        project(":core:analytics"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    implementation(libs.rxkotlin)
    implementation(libs.rxandroid)
    implementation("androidx.paging:paging-runtime:3.3.6")
    implementation("androidx.paging:paging-rxjava3:3.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.11.0")

    testImplementation(libs.androidx.paging.testing)
}