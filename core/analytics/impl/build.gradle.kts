plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.analytics.impl"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    api(project(":core:model"))
    api(project(":core:analytics:api"))
}