plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.android.library.compose)
    alias(libs.plugins.bowoon.android.feature)
}

android {
    namespace = "com.bowoon.movie.feature.home"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.domain,
        projects.core.datastore,
        projects.core.model,
        projects.core.notifications,
        projects.core.firebase,
        libs.androidx.navigation.compose,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.compose.paging,
        libs.threetenabp
    ).forEach {
        implementation(it)
    }
}