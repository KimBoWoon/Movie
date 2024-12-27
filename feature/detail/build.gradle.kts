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
        libs.androidx.navigation.compose,
        libs.androidx.compose.hilt.navigation,
        "androidx.media3:media3-exoplayer:1.5.0",
        "androidx.media3:media3-ui:1.5.0",
        "com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1"
    ).forEach {
        implementation(it)
    }
}