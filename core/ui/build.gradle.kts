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
        projects.core.analytics,
        libs.androidx.compose.material3,
        libs.androidx.compose.material3.adaptive,
        libs.androidx.compose.material3.navigationSuite,
        libs.androidx.compose.material3.icons,
        libs.coil.okhttp,
        libs.coil.compose,
        libs.androidx.compose.constraintLayout,
        libs.lottie.compose,
        libs.youtube.player,
        libs.androidx.compose.paging,
    ).forEach {
        implementation(it)
    }
}