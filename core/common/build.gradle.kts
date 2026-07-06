plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.common"
}

dependencies {
    arrayOf(
        libs.kotlinx.coroutines.test,
        libs.turbine
    ).forEach {
        testImplementation(it)
    }

    implementation(libs.rxkotlin)
    implementation(libs.rxandroid)
}