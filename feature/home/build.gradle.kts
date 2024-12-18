plugins {
    id("bowoon.library")
    id("bowoon.library.compose")
    id("bowoon.hilt")
    id("bowoon.android.feature")
}

android {
    namespace = "com.bowoon.home"
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
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }
}