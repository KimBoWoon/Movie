plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.notifications"
}

dependencies {
    api(project(":core:model"))

    arrayOf(
        project(":core:common"),
        project(":core:domain"),
        libs.coil.compose
    ).forEach {
        implementation(it)
    }

    implementation(libs.rxkotlin)
    implementation(libs.rxandroid)

    compileOnly(platform(libs.androidx.compose.bom))
}