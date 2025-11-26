plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.testing"
}

dependencies {
    api(projects.core.common)
    api(projects.core.data)
    api(projects.core.model)
    api(libs.kotlinx.coroutines.test)
    implementation(libs.hilt.android.testing)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.compose.paging)
    implementation(libs.androidx.paging.testing)
}