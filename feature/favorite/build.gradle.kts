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
        libs.androidx.compose.paging,
        libs.rxkotlin,
        libs.rxandroid,
        libs.androidx.paging.runtime,
        libs.paging.rxjava3,
        libs.kotlinx.coroutines.rx3
    ).forEach {
        implementation(it)
    }

    testImplementation(libs.androidx.paging.testing)
}