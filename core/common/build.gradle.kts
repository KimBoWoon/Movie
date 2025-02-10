plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.android.library.compose)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.common"
}

dependencies {
    implementation(libs.threetenabp)

    arrayOf(
        libs.kotlinx.coroutines.test,
        libs.turbine
    ).forEach {
        testImplementation(it)
    }
}