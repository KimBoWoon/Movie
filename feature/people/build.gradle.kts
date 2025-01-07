plugins {
    id("bowoon.library")
    id("bowoon.library.compose")
    id("bowoon.hilt")
    id("bowoon.android.feature")
}

android {
    namespace = "com.bowoon.people"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.domain,
        projects.core.model,
        libs.androidx.navigation.compose,
        libs.androidx.compose.hilt.navigation
    ).forEach {
        implementation(it)
    }
}