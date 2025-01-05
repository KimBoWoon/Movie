plugins {
    id("bowoon.library")
    id("bowoon.hilt")
}

android {
    namespace = "com.bowoon.domain"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.model,
        projects.core.data,
        libs.threetenabp,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }
}