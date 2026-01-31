plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.data"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.10.2")
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