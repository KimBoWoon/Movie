plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.firebase.impl"
}

dependencies {
    arrayOf(
        platform(libs.firebase.bom),
        libs.firebase.crashlytics
    ).forEach {
        implementation(it)
    }

    api(project(":core:firebase:api"))
}