plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
}

android {
    namespace = "com.cheeke.surfy.core.firebase.api"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(project(":core:common"))
}