plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.datamanager.impl"

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
        project(":core:database"),
        project(":core:datastore"),
        project(":core:network"),
        project(":core:datamanager:api")
    ).forEach {
        api(it)
    }

    implementation(project(":core:userdata:api"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(project(":core:datastore-test"))

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.serialization.json)
    androidTestImplementation(project(":core:testing"))
}