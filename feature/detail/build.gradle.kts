plugins {
    id("bowoon.library")
    id("bowoon.library.compose")
    id("bowoon.hilt")
    id("bowoon.android.feature")
}

android {
    namespace = "com.bowoon.detail"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.domain,
        projects.core.datastore,
        projects.core.model,
        projects.feature.people,
        libs.androidx.navigation.compose,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.media3.exoplayer,
        libs.androidx.media3.ui,
        libs.youtube.player
    ).forEach {
        implementation(it)
    }
}