plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.notifications.impl"
}

dependencies {
    api(project(":core:model"))
    api(project(":core:notifications:api"))

    arrayOf(
        project(":core:common"),
        project(":core:userdata:api"),
        project(":core:designsystem"),
        libs.coil.compose
    ).forEach {
        implementation(it)
    }

    compileOnly(platform(libs.androidx.compose.bom))
}