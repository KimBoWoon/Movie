plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.sync.impl"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:model"),
        project(":core:network"),
        project(":feature:detail:api"),
        project(":core:notifications:api"),
        project(":core:userdata:api"),
        libs.androidx.work.ktx,
        libs.hilt.ext.work
    ).forEach {
        implementation(it)
    }

    api(project(":core:sync:api"))

    ksp(libs.hilt.ext.compiler)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.work.testing)
}