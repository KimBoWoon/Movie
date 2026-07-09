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
        libs.coil.compose,
        libs.rxkotlin,
        libs.rxandroid
    ).forEach {
        implementation(it)
    }

    compileOnly(platform(libs.androidx.compose.bom))
}