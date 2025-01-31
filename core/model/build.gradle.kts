plugins {
    alias(libs.plugins.bowoon.android.library)
}

android {
    namespace = "com.bowoon.movie.core.model"
}

dependencies {
    arrayOf(
        libs.kotlinx.serialization.json
    ).forEach {
        implementation(it)
    }
}