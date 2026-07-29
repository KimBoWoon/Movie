import com.cheeke.surfy.convention.Config

plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.network.api"

    defaultConfig {
        buildConfigField("String", "TMDB_OPEN_API_KEY", "\"${Config.getProp("tmdb_open_api_key")}\"")
    }
}

dependencies {
    arrayOf(
        project(":core:common"),
        libs.okhttp.okhttp,
        libs.okhttp.profiler,
        libs.okhttp.logging,
        platform(libs.okhttp.bom)
    ).forEach {
        implementation(it)
    }

    arrayOf(
        project(":core:model")
    ).forEach {
        api(it)
    }
}