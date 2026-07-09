plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.sync"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:model"),
        project(":core:network"),
        project(":core:data"),
        project(":core:notifications"),
        libs.androidx.work.ktx,
        libs.hilt.ext.work,
        libs.work.rxjava3
    ).forEach {
        implementation(it)
    }

    ksp(libs.hilt.ext.compiler)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.work.testing)
}