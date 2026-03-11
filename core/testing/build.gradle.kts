plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.testing"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:data"),
        project(":core:model"),
        project(":core:analytics"),
        libs.kotlinx.coroutines.test
    ).forEach {
        api(it)
    }

    implementation(libs.hilt.android.testing)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.compose.paging)
    implementation(libs.androidx.paging.testing)
}