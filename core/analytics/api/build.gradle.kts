plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.analytics.api"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
//    api(project(":core:model"))
}