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

    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(projects.core.datastoreTest)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.serialization.json)
    androidTestImplementation(projects.core.testing)

    testImplementation(libs.retrofit2)
    testImplementation(libs.kotlinx.serialization.converter)
    testImplementation(libs.mockwebserver)
    androidTestImplementation(libs.mockwebserver)
}