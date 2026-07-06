plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.room)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.database"
}

dependencies {
    arrayOf(
        project(":core:common"),
        libs.kotlinx.serialization.json,
        libs.androidx.junit,
        libs.androidx.datastore,
        libs.androidx.room.runtime,
//        libs.androidx.room.ktx,
        libs.androidx.compose.room.paging
    ).forEach {
        implementation(it)
    }

    implementation("androidx.paging:paging-runtime:3.3.6")
    implementation("androidx.paging:paging-rxjava3:3.3.6")
    implementation("androidx.room:room-rxjava3:2.8.4")
    implementation(libs.rxkotlin)
    implementation(libs.rxandroid)

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