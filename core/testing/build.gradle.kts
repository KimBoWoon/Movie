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
        project(":core:model"),
        project(":core:analytics:api"),
        libs.kotlinx.coroutines.test
    ).forEach {
        api(it)
    }

    implementation(project(":feature:detail:api"))
    implementation(project(":feature:search:api"))
    implementation(project(":core:sync:api"))
    implementation(project(":core:datamanager:api"))
    implementation(project(":core:userdata:api"))
    implementation(libs.hilt.android.testing)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.compose.paging)
    implementation(libs.androidx.paging.testing)
}