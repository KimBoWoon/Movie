plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.data"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    arrayOf(
        libs.androidx.compose.paging,
        libs.rxkotlin,
        libs.rxandroid,
        libs.androidx.paging.runtime,
        libs.paging.rxjava3,
        libs.kotlinx.coroutines.rx3
    ).forEach {
        implementation(it)
    }

    arrayOf(
        project(":core:common"),
        project(":core:database"),
        project(":core:datastore"),
        project(":core:network")
    ).forEach {
        api(it)
    }

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(project(":core:datastore-test"))

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.serialization.json)
    androidTestImplementation(project(":core:testing"))
}