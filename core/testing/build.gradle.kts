plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.testing"
}

dependencies {
    api(projects.core.common)
    api(projects.core.data)
    api(projects.core.model)
    api(projects.core.analytics)
    api(libs.kotlinx.coroutines.test)
    implementation(libs.hilt.android.testing)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.compose.paging)
    implementation(libs.androidx.paging.testing)
}