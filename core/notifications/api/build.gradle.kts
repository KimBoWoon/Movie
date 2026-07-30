plugins {
    alias(libs.plugins.cheeke.android.library)
}

android {
    namespace = "com.cheeke.surfy.core.notifications.api"
}

dependencies {
    api(project(":core:model"))

    compileOnly(platform(libs.androidx.compose.bom))
}