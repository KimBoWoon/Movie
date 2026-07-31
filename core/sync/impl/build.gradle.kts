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
        project(":core:network:api"),
        project(":feature:detail:api"),
        project(":core:notifications:api"),
        project(":core:userdata:api"),
        project(":core:database:impl"),
        project(":core:designsystem"),
        libs.androidx.work.ktx,
        libs.hilt.ext.work
    ).forEach {
        implementation(it)
    }

    api(project(":core:sync:api"))

    ksp(libs.hilt.ext.compiler)

    testImplementation(testFixtures(project(":core:network:api")))
    testImplementation(testFixtures(project(":core:sync:api")))
    testImplementation(testFixtures(project(":core:userdata:api")))

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(testFixtures(project(":core:userdata:api")))
    androidTestImplementation(testFixtures(project(":core:network:api")))
    androidTestImplementation(testFixtures(project(":core:sync:api")))
    androidTestImplementation(testFixtures(project(":feature:detail:api")))
}