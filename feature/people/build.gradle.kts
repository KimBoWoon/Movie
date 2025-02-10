plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.android.library.compose)
    alias(libs.plugins.bowoon.android.feature)
}

android {
    namespace = "com.bowoon.movie.feature.people"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.domain,
        projects.core.model,
        projects.core.firebase,
        libs.androidx.navigation.compose,
        libs.androidx.compose.hilt.navigation
    ).forEach {
        implementation(it)
    }
}