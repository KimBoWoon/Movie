plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.datamanager.api"

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

    arrayOf(
        project(":core:common"),
        project(":core:database:impl"),
        project(":core:network")
    ).forEach {
        api(it)
    }

    implementation(project(":core:userdata:api"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.androidx.paging.testing)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.serialization.json)
    androidTestImplementation(project(":core:testing"))
}