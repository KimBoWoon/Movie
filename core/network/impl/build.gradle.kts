plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.network.impl"
}

dependencies {
    arrayOf(
        project(":core:common"),
        libs.kotlinx.serialization.converter,
        libs.kotlinx.serialization.json,
        libs.retrofit2,
        libs.okhttp.okhttp,
        libs.okhttp.profiler,
        libs.okhttp.logging,
        platform(libs.okhttp.bom)
    ).forEach {
        implementation(it)
    }

    arrayOf(
        project(":core:model"),
        project(":core:network:api")
    ).forEach {
        api(it)
    }

    arrayOf(
        project(":core:testing"),
        testFixtures(project(":core:network:api")),
        libs.retrofit2,
        libs.kotlinx.serialization.converter,
        libs.okhttp.mockWebServer,
        libs.io.mockk
    ).forEach {
        testImplementation(it)
    }

    androidTestImplementation(libs.okhttp.mockWebServer)
}