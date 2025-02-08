plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.android.library.compose)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.firebase"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data
    ).forEach {
        implementation(it)
    }
}