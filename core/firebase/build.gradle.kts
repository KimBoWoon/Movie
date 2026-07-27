plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.firebase"
}

dependencies {
    arrayOf(
        project(":core:common"),
    ).forEach {
        implementation(it)
    }
}