plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.notifications"
}

dependencies {
    api(projects.core.model)

    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(libs.coil.compose)

    compileOnly(platform(libs.androidx.compose.bom))
}