plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.surfy.core.notifications"
}

dependencies {
    api(projects.core.model)

    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(libs.coil.compose)

    compileOnly(platform(libs.androidx.compose.bom))
}