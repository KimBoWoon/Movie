plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.search.impl"
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:database:impl"),
        project(":core:model"),
        project(":core:firebase:api"),
        project(":core:analytics:api"),
        project(":core:datamanager:api"),
        project(":core:network:api"),
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    api(project(":feature:search:api"))

    testImplementation(libs.androidx.paging.testing)
    androidTestImplementation(libs.androidx.paging.testing)

    testImplementation(testFixtures(notation = project(":feature:detail:api")))
    testImplementation(testFixtures(notation = project(":core:userdata:api")))
    testImplementation(testFixtures(notation = project(":core:datamanager:api")))
    testImplementation(testFixtures(notation = project(":core:network:api")))
    testImplementation(testFixtures(notation = project(":core:analytics:api")))
    testImplementation(testFixtures(notation = project(":feature:search:api")))
}