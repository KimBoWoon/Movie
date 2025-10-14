plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.android.library.compose)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.ui"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.model,
        projects.core.data,
        libs.androidx.compose.material3,
        libs.androidx.compose.material3.adaptive,
        libs.androidx.compose.material3.navigationSuite,
        libs.androidx.compose.material3.icons,
        libs.coil.okhttp,
        libs.coil.compose,
        libs.threetenabp,
        libs.androidx.compose.constraintLayout,
        libs.lottie.compose
    ).forEach {
        implementation(it)
    }
}