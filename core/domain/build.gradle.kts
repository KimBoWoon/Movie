plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.domain"
}

dependencies {
    arrayOf(
        projects.core.common,
        libs.threetenabp,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    api(projects.core.data)
    api(projects.core.model)
}