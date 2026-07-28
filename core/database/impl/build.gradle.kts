plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.room)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.database.impl"
}

dependencies {
    arrayOf(
        project(":core:common"),
        libs.kotlinx.serialization.json,
        libs.androidx.junit,
        libs.androidx.datastore,
        libs.androidx.room.runtime,
        libs.androidx.room.ktx,
        libs.androidx.compose.room.paging
    ).forEach {
        implementation(it)
    }

    api(project(":core:database:api"))
    api(project(":core:model"))

    arrayOf(
        libs.androidx.room.compiler
    ).forEach {
        ksp(it)
    }

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}