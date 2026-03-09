plugins {
    alias(libs.plugins.bowoon.android.library)
}

android {
    namespace = "com.bowoon.surfy.core.model"
}

dependencies {
    arrayOf(
        libs.kotlinx.serialization.json,
        libs.androidx.paging.common
    ).forEach {
        implementation(it)
    }
}