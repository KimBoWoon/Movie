plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.favorite.api"
}

dependencies {
    arrayOf(
        libs.androidx.navigation3.runtime
    ).forEach {
        implementation(it)
    }
}