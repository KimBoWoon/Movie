plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.ui"
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