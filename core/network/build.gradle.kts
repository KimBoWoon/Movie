import com.cheeke.convention.Config

plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.network"

    defaultConfig {
        buildConfigField("String", "TMDB_OPEN_API_KEY", "\"${Config.getProp("tmdb_open_api_key")}\"")
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

    testImplementation(projects.core.testing)
    testImplementation(libs.retrofit2)
    testImplementation(libs.kotlinx.serialization.converter)
    testImplementation(libs.mockwebserver)
    androidTestImplementation(libs.mockwebserver)
}