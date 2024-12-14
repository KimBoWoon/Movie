plugins {
    id("bowoon.library")
    id("bowoon.library.compose")
    id("bowoon.hilt")
}

android {
    namespace = "com.bowoon.ui"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.model,
        libs.androidx.compose.material3,
        libs.androidx.compose.material3.adaptive,
        libs.androidx.compose.material3.navigationSuite,
        libs.coil.okhttp,
        libs.coil.compose
    ).forEach {
        implementation(it)
    }
}