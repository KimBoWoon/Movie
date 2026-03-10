plugins {
    alias(libs.plugins.cheeke.android.library)
}

android {
    namespace = "com.cheeke.surfy.core.model"
}

dependencies {
    arrayOf(
        libs.kotlinx.serialization.json,
        libs.androidx.paging.common
    ).forEach {
        implementation(it)
    }
}