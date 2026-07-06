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
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    implementation(libs.rxkotlin)
    implementation(libs.rxandroid)
    implementation("androidx.paging:paging-runtime:3.3.6")
    implementation("androidx.paging:paging-rxjava3:3.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.11.0")

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