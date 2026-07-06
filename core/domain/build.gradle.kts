plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.domain"
}

dependencies {
    arrayOf(
        project(":core:common")
    ).forEach {
        implementation(it)
    }

    implementation(libs.rxkotlin)
    implementation(libs.rxandroid)

    arrayOf(
        project(":core:data"),
        project(":core:model")
    ).forEach {
        api(it)
    }

    testImplementation(libs.androidx.paging.testing)
}