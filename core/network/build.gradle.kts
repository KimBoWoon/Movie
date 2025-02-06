import com.bowoon.convention.Config

plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.network"

    defaultConfig {
        buildConfigField("String", "TMDB_OPEN_API_KEY", "\"${Config.getProp("tmdb_open_api_key")}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    arrayOf(
        projects.core.common,
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
        projects.core.model
    ).forEach {
        api(it)
    }

//    testImplementation(projects.core.testing)
    testImplementation(libs.retrofit2)
    testImplementation(libs.kotlinx.serialization.converter)
    testImplementation(libs.mockwebserver)
    androidTestImplementation(libs.mockwebserver)
}